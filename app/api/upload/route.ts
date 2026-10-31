import { env } from 'cloudflare:workers';
import { getChatGPTUser } from '../../chatgpt-auth';
export async function POST(req:Request){
 if(!await getChatGPTUser())return Response.json({error:'Sign in required'},{status:401});
 if(req.headers.get('origin') && req.headers.get('origin')!==new URL(req.url).origin)return Response.json({error:'Invalid origin'},{status:403});
 const form=await req.formData();const f=form.get('file');
 if(!(f instanceof File)||f.size>8*1024*1024||!['image/jpeg','image/png','image/webp','application/pdf'].includes(f.type))return Response.json({error:'JPG, PNG, WebP অথবা PDF দিন (সর্বোচ্চ ৮ MB)।'},{status:400});
 if(!env.BUCKET)return Response.json({error:'Storage unavailable'},{status:503});
 const id=crypto.randomUUID();await env.BUCKET.put(id,await f.arrayBuffer(),{httpMetadata:{contentType:f.type}});
 return Response.json({url:'/api/media/'+id,name:f.name});
}
