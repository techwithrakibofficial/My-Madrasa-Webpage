/** Device-only storage for the bundled Android edition. Hosted website requests stay on the server. */
const reply=(data:unknown,status=200)=>new Response(JSON.stringify(data),{status,headers:{'Content-Type':'application/json'}});
const kinds=['notice','routine','result','student','admission','attendance','fee','gallery','teacher','settings','syllabus'];
function database():Promise<IDBDatabase>{return new Promise((resolve,reject)=>{const req=indexedDB.open('noorani-madrasa-device',1);req.onupgradeneeded=()=>req.result.createObjectStore('records',{keyPath:'id'});req.onsuccess=()=>resolve(req.result);req.onerror=()=>reject(Error('ফোনে তথ্য সংরক্ষণ করা যাচ্ছে না।'));});}
export async function localRows():Promise<any[]>{const db=await database();try{return await new Promise((resolve,reject)=>{const r=db.transaction('records','readonly').objectStore('records').getAll();r.onsuccess=()=>resolve(r.result);r.onerror=()=>reject(r.error)});}finally{db.close()}}
async function mutate(row:any,remove=false){const db=await database();try{await new Promise<void>((resolve,reject)=>{const t=db.transaction('records','readwrite');const s=t.objectStore('records');if(remove)s.delete(row.id);else s.put(row);t.oncomplete=()=>resolve();t.onerror=()=>reject(t.error);t.onabort=()=>reject(t.error)});}finally{db.close()}}
export async function restoreLocal(rows:any[]){if(!Array.isArray(rows)||rows.length>15000||rows.some(r=>typeof r.id!=='string'||!kinds.includes(r.kind)||!r.data||typeof r.data!=='object'))throw Error('ব্যাকআপ ফাইল সঠিক নয়।');const db=await database();try{await new Promise<void>((resolve,reject)=>{const t=db.transaction('records','readwrite');const s=t.objectStore('records');rows.forEach(r=>s.put(r));t.oncomplete=()=>resolve();t.onerror=()=>reject(t.error);t.onabort=()=>reject(t.error)});}finally{db.close()}}
export async function apiFetch(url:string,options:RequestInit|undefined,offline:boolean):Promise<Response>{
 if(!offline)return fetch(url,options);
 try{
  if(url==='/api/upload'){
   const file=(options?.body as FormData).get('file');if(!(file instanceof File)||file.size>8*1024*1024||!['image/jpeg','image/png','image/webp','application/pdf'].includes(file.type))return reply({error:'JPG, PNG, WebP অথবা PDF দিন (সর্বোচ্চ ৮ MB)।'},400);
   const data=await new Promise<string>((resolve,reject)=>{const r=new FileReader();r.onload=()=>resolve(String(r.result));r.onerror=reject;r.readAsDataURL(file)});return reply({url:data,name:file.name});
  }
  const parsed=new URL(url,'https://appassets.androidplatform.net');
  if(options?.method==='POST'){
   const b=JSON.parse(String(options.body));if(!kinds.includes(b.kind)||!b.data||typeof b.data!=='object')throw Error('তথ্য সঠিক নয়।');
   if(b.action==='delete'){await mutate({id:b.id},true);return reply({ok:true})}
   if(b.data.url&&!/^(https?:\/\/|data:(image\/(png|jpeg|webp)|application\/pdf);base64,)/.test(b.data.url))throw Error('ফাইলের লিংক সঠিক নয়।');
   if(b.kind==='result'&&['quran','arabic','bangla','math'].some(k=>!Number.isFinite(Number(b.data[k]))||Number(b.data[k])<0||Number(b.data[k])>100))throw Error('নম্বর ০ থেকে ১০০ এর মধ্যে দিন।');
   if(b.kind==='admission'&&b.data.gender==='ছাত্রী'&&b.data.residential==='আবাসিক')throw Error('আবাসিক সুবিধা শুধু ছাত্রদের জন্য।');
   const id=b.id||(typeof crypto.randomUUID==='function'?crypto.randomUUID():Array.from(crypto.getRandomValues(new Uint8Array(16)),x=>x.toString(16).padStart(2,'0')).join(''));const data={...b.data};delete data.id;await mutate({id,kind:b.kind,data,createdAt:Date.now()});return reply({id,...data});
  }
  const kind=parsed.searchParams.get('kind');if(!kind||!kinds.includes(kind))throw Error('বিভাগ পাওয়া যায়নি।');return reply((await localRows()).filter(r=>r.kind===kind).sort((a,b)=>b.createdAt-a.createdAt).map(r=>({id:r.id,...r.data})));
 }catch(e:any){return reply({error:e.message||'তথ্য সংরক্ষণ হয়নি। ফোনের খালি জায়গা ও ব্যাকআপ দেখুন।'},400)}
}
export function saveNative(name:string,mime:string,text:string){const native=(window as any).MadrasaNative;if(!native)return false;const bytes=new TextEncoder().encode(text);let binary='';for(let i=0;i<bytes.length;i++)binary+=String.fromCharCode(bytes[i]);native.saveFile(name,mime,btoa(binary));return true;}
