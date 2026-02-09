import { useParams } from 'react-router-dom';

export default function ProductDetails() {
    const { id } = useParams<{ id: string }>(); // Strongly typed params

    return (
        <div className="bg-white rounded-3xl p-8 shadow-sm border border-slate-100">
            <h2 className="text-2xl font-bold mb-4">Comparison Dashboard</h2>
            <div className="bg-slate-50 p-4 rounded-xl text-slate-600 font-mono text-sm">
                Viewing Master Product ID: {id}
            </div>
        </div>
    );
}