import { getDb } from '../../../db';
import { records } from '../../../db/schema';
import { eq, desc } from 'drizzle-orm';
import { getChatGPTUser } from '../../chatgpt-auth';
export const dynamic = 'force-dynamic';
const kinds = ['notice','routine','result','student','admission','attendance','fee','gallery','teacher','settings','syllabus'];
const publicKinds = ['notice','routine','gallery','teacher','settings','syllabus'];
const json = (d:unknown,status=200)=>Response.json(d,{status,headers:{'Cache-Control':'no-store'}});
export async function GET(req:Request){
 const kind=new URL(req.url).searchParams.get('kind')||'notice';
 if(!kinds.includes(kind))return json({error:'Invalid category'},400);
 if(!publicKinds.includes(kind)&&!await getChatGPTUser())return json({error:'Sign in required'},401);
 try {const rows=await getDb().select().from(records).where(eq(records.kind,kind)).orderBy(desc(records.createdAt));return json(rows.map(r=>({id:r.id,...JSON.parse(r.payload)})));}
 catch{return json({error:'তথ্য লোড করা যায়নি। আবার চেষ্টা করুন।'},503);}
}
// Administrative writes rely on the confirmed owner-private Sites access boundary.
// Do not change this Site to public until explicit administrator roles are configured.
export async function POST(req:Request){
 if(!await getChatGPTUser())return json({error:'Sign in required'},401);
 if(req.headers.get('origin') && req.headers.get('origin')!==new URL(req.url).origin)return json({error:'Invalid origin'},403);
 try{
 const body:any=await req.json();const {kind,data}=body;
 if(!kinds.includes(kind)||!data||Array.isArray(data)||typeof data!=='object'||JSON.stringify(data).length>40000)return json({error:'Invalid data'},400);
 if(body.action==='delete') {if(typeof body.id!=='string')return json({error:'ID required'},400);await getDb().delete(records).where(eq(records.id,body.id));return json({ok:true});}
 if(data.url && (typeof data.url!=='string'||!(/^https?:\/\//.test(data.url)||/^\/api\/media\/[a-f0-9-]+$/.test(data.url))))return json({error:'Invalid file URL'},400);
 if(kind==='result'&&['quran','arabic','bangla','math'].some(k=>!Number.isFinite(Number(data[k]))||Number(data[k])<0||Number(data[k])>100))return json({error:'নম্বর ০ থেকে ১০০ এর মধ্যে দিন'},400);
 if(kind==='admission'&&data.gender==='ছাত্রী'&&data.residential==='আবাসিক')return json({error:'আবাসিক সুবিধা শুধু ছাত্রদের জন্য'},400);
 const id=typeof body.id==='string'?body.id:crypto.randomUUID();
 const payload={...data};delete payload.id;
 await getDb().insert(records).values({id,kind,payload:JSON.stringify(payload),createdAt:Date.now()}).onConflictDoUpdate({target:records.id,set:{payload:JSON.stringify(payload)}});
 return json({id,...payload});
 }catch{return json({error:'সংরক্ষণ হয়নি। তথ্য যাচাই করে আবার চেষ্টা করুন।'},400);}
}
