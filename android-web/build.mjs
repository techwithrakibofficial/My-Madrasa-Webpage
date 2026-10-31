import {build} from 'vite';
import react from '@vitejs/plugin-react';
import {fileURLToPath} from 'node:url';
import {mkdir,writeFile,readFile} from 'node:fs/promises';
import {createRequire} from 'node:module';
import {execFileSync} from 'node:child_process';
import path from 'node:path';
const root=path.dirname(fileURLToPath(import.meta.url));const req=createRequire(import.meta.url);
const fontDir=path.join(root,'public/fonts');await mkdir(fontDir,{recursive:true});
const css=execFileSync('curl',['--fail','--location','--retry','2','--max-time','45','--silent','https://fonts.googleapis.com/css2?family=Noto+Sans+Bengali:wght@400;500;600;700&display=swap'],{encoding:'utf8'});
let fontCss=css;let i=0;for(const url of new Set([...css.matchAll(/url\((https:[^)]+)\)/g)].map(m=>m[1]))){const filename='bengali-'+(i++)+'.'+(url.includes('.woff2')?'woff2':'ttf');execFileSync('curl',['--fail','--location','--retry','2','--max-time','60','--silent','--output',path.join(fontDir,filename),url]);fontCss=fontCss.split(url).join('./fonts/'+filename)}
await writeFile(path.join(root,'public/fonts.css'),fontCss);
await build({root,base:'./',configFile:false,css:{postcss:{plugins:[]}},plugins:[{name:'offline-styles',enforce:'pre',transform(code,id){if(id.endsWith('/app/globals.css'))return code.replace(/^@import.*fonts\.googleapis.*$/gm,'').replace(/@import 'tailwindcss';/g,'');}},react()],resolve:{alias:{'react':path.dirname(req.resolve('react/package.json')),'react-dom':path.dirname(req.resolve('react-dom/package.json')),'lucide-react':path.dirname(req.resolve('lucide-react/package.json'))}},build:{target:'chrome95',outDir:path.join(root,'../android/app/src/main/assets/www'),emptyOutDir:true}});
const index=path.join(root,'../android/app/src/main/assets/www/index.html');await writeFile(index,(await readFile(index,'utf8')).replace('</head>','<link rel="stylesheet" href="./fonts.css"></head>'));
console.log('Android UI bundled locally, including Bengali fonts.');
