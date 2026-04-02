import { useBasketTrends } from "@/hooks/useTrends";
import { useBasketItems } from "@/hooks/useTrends";
import { formatCurrency } from "@/lib/formatters";
import { format, parseISO } from "date-fns";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer
} from "recharts";
import {TrendingUp, Loader2, Info, ShoppingCart, CheckCircle2} from "lucide-react";

type ChartDataPoint = {
    date: string;
    [storeName: string]: string | number;
};

const STORE_COLORS: Record<string, string> = {
    "Checkers": "#38A8AE",
    "Shoprite": "#ea212d",
    "Pick n Pay": "#003359",
    "default": "#64748b"
};

export function PriceTrendsPage() {
    const { data, isLoading } = useBasketTrends();
    const { data: basketItems } = useBasketItems();

    const chartData = (data || []).reduce<ChartDataPoint[]>((acc, curr) => {
        const dateKey = format(parseISO(curr.logDate), "d MMM");
        const existing = acc.find((e) => e.date === dateKey);

        if (existing) {
            existing[curr.storeName] = curr.basketPrice;
        } else {
            acc.push({
                date: dateKey,
                [curr.storeName]: curr.basketPrice
            });
        }
        return acc;
    }, []);

    const stores = Array.from(new Set(data?.map(d => d.storeName) || []));

    const actualItemCount = basketItems?.length || 0;

    let cheapestStore = "Calculating...";
    if (chartData.length > 0) {
        const latestData = chartData[chartData.length - 1];
        let minPrice = Infinity;

        stores.forEach(store => {
            const storePrice = latestData[store] as number;
            if (storePrice && storePrice < minPrice) {
                minPrice = storePrice;
                cheapestStore = store;
            }
        });
    }

    let volatility = "Stable";
    let volatilityColor = "text-emerald-500";

    if (chartData.length > 1) {
        const oldestData = chartData[0];
        const newestData = chartData[chartData.length - 1];

        const storeToTrack = stores[0];
        const oldPrice = oldestData[storeToTrack] as number;
        const newPrice = newestData[storeToTrack] as number;

        if (oldPrice && newPrice) {
            const percentChange = Math.abs((newPrice - oldPrice) / oldPrice) * 100;
            if (percentChange > 5) {
                volatility = "High";
                volatilityColor = "text-red-500";
            } else if (percentChange > 2) {
                volatility = "Moderate";
                volatilityColor = "text-orange-500";
            }
        }
    }

    return (
        <div className="container mx-auto px-4 py-8 max-w-6xl">
            {/* HERO */}
            <div className="mb-8 rounded-2xl bg-slate-900 p-8 text-white shadow-xl">
                <div className="flex items-center gap-3 mb-4">
                    <TrendingUp className="h-8 w-8 text-indigo-400" />
                    <h1 className="text-3xl font-extrabold tracking-tight sm:text-4xl">
                        Market Price Trends
                    </h1>
                </div>
                <p className="max-w-2xl text-lg text-slate-300">
                    Track the real cost of living in South Africa. We monitor a dynamic basket of everyday essential items across major retailers to show you who is actually fighting inflation.
                </p>
            </div>

            {/* SUMMARY CARDS */}
            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-8">

                {/* DYNAMIC CHEAPEST STORE */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
                    <p className="text-sm text-slate-500 font-medium">Cheapest Store Today</p>
                    <h3 className="text-2xl font-bold text-indigo-600">
                        {isLoading ? "..." : cheapestStore}
                    </h3>
                    <p className="text-xs text-slate-400 mt-1">Based on today's basket total</p>
                </div>

                {/* DYNAMIC ITEM COUNT */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
                    <p className="text-sm text-slate-500 font-medium">Items in Comparison</p>
                    <h3 className="text-2xl font-bold text-slate-800">
                        {isLoading ? "..." : `${actualItemCount} Products`}
                    </h3>
                    <p className="text-xs text-slate-400 mt-1">Identical items across stores</p>
                </div>

                {/* DYNAMIC VOLATILITY */}
                <div className="bg-white p-6 rounded-2xl border border-slate-200 shadow-sm">
                    <p className="text-sm text-slate-500 font-medium">Market Volatility</p>
                    <h3 className={`text-2xl font-bold ${volatilityColor}`}>
                        {isLoading ? "..." : volatility}
                    </h3>
                    <p className="text-xs text-slate-400 mt-1">7-day price fluctuation</p>
                </div>

            </div>


            {/* CHART CARD */}
            <div className="rounded-2xl border border-slate-200 bg-white p-6 shadow-sm">
                <div className="mb-6 flex items-start justify-between">
                    <div>
                        <h2 className="text-xl font-bold text-slate-800">The "OptiPrice" Essential Basket Index</h2>
                        <p className="text-sm text-slate-500 mt-1">Total cost of 10 identical everyday items at each store over time.</p>
                    </div>
                    <div className="hidden sm:flex items-center gap-2 rounded-lg bg-indigo-50 px-3 py-2 text-xs font-medium text-indigo-700">
                        <Info size={14} /> Dynamically calculated daily
                    </div>
                </div>

                {isLoading ? (
                    <div className="flex h-100 items-center justify-center">
                        <Loader2 className="h-8 w-8 animate-spin text-indigo-500" />
                    </div>
                ) : chartData.length > 0 ? (
                    <div className="h-100 w-full mt-4">
                        <ResponsiveContainer width="100%" height="100%">
                            <LineChart data={chartData} margin={{ top: 10, right: 10, left: 10, bottom: 10 }}>
                                <CartesianGrid strokeDasharray="3 3" stroke="#f1f5f9" vertical={false} />
                                <XAxis
                                    dataKey="date"
                                    stroke="#94a3b8"
                                    fontSize={12}
                                    tickMargin={10}
                                    axisLine={false}
                                    tickLine={false}
                                />
                                <YAxis
                                    stroke="#94a3b8"
                                    fontSize={12}
                                    tickFormatter={(val) => `R${val}`}
                                    axisLine={false}
                                    tickLine={false}
                                    domain={['auto', 'auto']}
                                />
                                <Tooltip
                                    formatter={(value: number | undefined) => [
                                        formatCurrency(value ?? 0),
                                        "Basket Total"
                                    ]}
                                    contentStyle={{
                                        borderRadius: "12px",
                                        border: "none",
                                        boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)"
                                    }}
                                />
                                <Legend iconType="circle" wrapperStyle={{ paddingTop: "20px" }} />

                                {stores.map((store) => (
                                    <Line
                                        key={store}
                                        type="monotone"
                                        dataKey={store}
                                        stroke={STORE_COLORS[store] || STORE_COLORS["default"]}
                                        strokeWidth={4}
                                        dot={{ r: 4, strokeWidth: 2, fill: "#fff" }}
                                        activeDot={{ r: 6, strokeWidth: 0 }}
                                        connectNulls={true}
                                    />
                                ))}
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                ) : (
                    <div className="flex h-75 flex-col items-center justify-center text-slate-400">
                        <TrendingUp size={48} className="mb-4 opacity-20" />
                        <p>Not enough historical data to generate trend lines yet.</p>
                    </div>
                )}
            </div>

            {/* "WHAT IS THIS?" SECTION */}
            <div className="mt-8 grid grid-cols-1 lg:grid-cols-3 gap-8">
                <div className="lg:col-span-2 bg-white p-8 rounded-2xl border border-slate-200 shadow-sm">
                    <h3 className="text-xl font-bold text-slate-800 mb-4 flex items-center gap-2">
                        <ShoppingCart className="text-indigo-600" />
                        What's in the basket?
                    </h3>
                    <p className="text-slate-600 mb-6">
                        To calculate the index, we track 10 identical products that are available at all three major retailers. This ensures the comparison is 100% fair.
                    </p>
                    <div className="grid grid-cols-1 sm:grid-cols-2 gap-3">
                        {basketItems?.map((item, index) => (
                            <div key={index} className="flex items-center gap-2 text-sm text-slate-700 bg-slate-50 p-3 rounded-lg border border-slate-100">
                                <CheckCircle2 size={16} className="text-emerald-500" />
                                {item}
                            </div>
                        ))}
                    </div>
                </div>
            </div>
        </div>
    );
}