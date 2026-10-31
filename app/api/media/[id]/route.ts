import { env } from 'cloudflare:workers';
export async function GET(_req:Request,{params}:{params:Promise<{id:string}>}){
 const {id}=await params;if(!/^[a-f0-9-]{36}$/.test(id))return new Response('Not found',{status:404});
 const f=await env.BUCKET?.get(id);if(!f)return new Response('Not found',{status:404});
 return new Response(f.body,{headers:{'Content-Type':f.httpMetadata?.contentType||'application/octet-stream','X-Content-Type-Options':'nosniff','Cache-Control':'private, max-age=3600'}});
}
