import { Outlet, Link, ScrollRestoration } from 'react-router-dom';
import { ShoppingBasket, Github } from 'lucide-react';

export default function RootLayout() {
    return (
        <div className="min-h-screen bg-slate-50 flex flex-col font-sans text-slate-900">
            <ScrollRestoration />

            <nav className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/80 backdrop-blur-md">
                <div className="mx-auto flex h-16 max-w-7xl items-center justify-between px-4 sm:px-6 lg:px-8">

                    <Link to="/" className="flex items-center gap-2 group">
                        <div className="flex h-10 w-10 items-center justify-center rounded-xl bg-indigo-600 text-white transition-transform group-hover:scale-110 shadow-lg shadow-indigo-200">
                            <ShoppingBasket size={24} />
                        </div>
                        <span className="text-xl font-bold tracking-tight bg-gradient-to-r from-indigo-600 to-violet-600 bg-clip-text text-transparent">
              OptiPrice
            </span>
                    </Link>

                    <div className="flex items-center gap-6">
                        <Link to="/" className="text-sm font-semibold text-slate-600 hover:text-indigo-600 transition-colors">
                            Search
                        </Link>
                        <a href="https://github.com" target="_blank" className="text-slate-400 hover:text-slate-900 transition-colors">
                            <Github size={20} />
                        </a>
                    </div>
                </div>
            </nav>

            <main className="grow mx-auto w-full max-w-7xl px-4 py-8 sm:px-6 lg:px-8">
                <Outlet />
            </main>

            <footer className="border-t border-slate-200 bg-white py-12">
                <div className="mx-auto max-w-7xl px-4 text-center">
                    <p className="text-sm text-slate-500 font-medium">
                        &copy; {new Date().getFullYear()} OptiPrice Engine. Full-Stack Data Aggregator.
                    </p>
                </div>
            </footer>
        </div>
    );
}