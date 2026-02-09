import { Link } from 'react-router-dom';
import { AlertCircle } from 'lucide-react';

export default function ErrorPage() {
    return (
        <div className="flex h-[80vh] flex-col items-center justify-center text-center">
            <AlertCircle size={64} className="mb-4 text-red-500" />
            <h1 className="text-4xl font-bold text-slate-900">404 - Page Not Found</h1>
            <p className="mt-2 text-lg text-slate-600">Oops! We couldn't find that product.</p>
            <Link to="/" className="mt-6 rounded-full bg-indigo-600 px-6 py-2 font-semibold text-white hover:bg-indigo-700">
                Go Home
            </Link>
        </div>
    );
}