import React from 'react';
import {createRoot} from 'react-dom/client';
import Madrasa from '../app/madrasa';
import '../app/globals.css';
const native=(window as any).MadrasaNative;
if(native){window.print=()=>native.printPage();document.addEventListener('click',e=>{const a=(e.target as HTMLElement).closest('a');if(!a)return;const href=a.getAttribute('href')||'';if(href.startsWith('data:')){e.preventDefault();const match=href.match(/^data:([^;]+);base64,(.*)$/);if(match)native.saveFile(a.download||('মাদ্রাসা-ফাইল.'+(match[1]==='application/pdf'?'pdf':'png')),match[1],match[2]);}});}
createRoot(document.getElementById('root')!).render(<Madrasa signedIn={true} offline={true}/>);
