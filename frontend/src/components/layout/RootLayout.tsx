import { Outlet, ScrollRestoration } from 'react-router-dom';
import { Navbar } from "./Navbar";
import { Footer } from "./Footer";

export default function RootLayout() {
    return (
        <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-900">
            <ScrollRestoration />

            <Navbar />

            <main className="flex-grow w-full">
                <Outlet />
            </main>

            <Footer />
        </div>
    );
}