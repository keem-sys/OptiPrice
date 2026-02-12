import { Outlet, ScrollRestoration } from 'react-router-dom';
import { Navbar } from './Navbar'; // Import the new component

export default function RootLayout() {
    return (
        <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-900">
            <ScrollRestoration />

            <Navbar />

            <main className="flex-grow w-full">
                <Outlet />
            </main>

            <footer className="border-t border-slate-200 bg-white py-8 mt-auto">
                <div className="container mx-auto px-4 text-center text-sm text-slate-500">
                    &copy; {new Date().getFullYear()} OptiPrice.
                </div>
            </footer>
        </div>
    );
}