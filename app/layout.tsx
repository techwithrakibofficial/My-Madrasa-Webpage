import type { Metadata } from 'next';
import './globals.css';
export const metadata:Metadata={title:'ঢেকুঞ্চা দিয়ল চারমাথা বাজার নূরানী হাফেজিয়া মাদ্রাসা',description:'ইলমের আলোয় আলোকিত হোক আগামী। প্রতিষ্ঠিত ২০১৮, আক্কেলপুর, জয়পুরহাট।',icons:{icon:'/favicon.svg'}};
export default function RootLayout({children}:{children:React.ReactNode}){return <html lang="bn"><body>{children}</body></html>}
