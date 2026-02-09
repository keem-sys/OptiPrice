import { Search } from 'lucide-react';

export default function Home() {
    return (
        <div className="flex flex-col items-center justify-center pt-20">
            <h1 className="text-5xl font-extrabold text-slate-900 mb-6 text-center">
                Smart Grocery <span className="text-indigo-600">Comparison</span>
            </h1>
            <p className="text-lg text-slate-600 mb-10 max-w-2xl text-center">
                Compare real-time prices across South Africa's biggest retailers using AI-powered matching.
            </p>

            {/* Search Input Placeholder */}
            <div className="w-full max-w-2xl relative">
                <Search className="absolute left-4 top-1/2 -translate-y-1/2 text-slate-400" size={20} />
                <input
                    type="text"
                    placeholder="Search for milk, bread, or brand name..."
                    className="w-full pl-12 pr-4 py-4 rounded-2xl border border-slate-200 bg-white shadow-xl shadow-slate-200/50 outline-none focus:ring-2 focus:ring-indigo-500 transition-all text-lg"
                />
            </div>
        </div>
    );
}