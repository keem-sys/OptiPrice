import { usePriceHistory } from "@/hooks/useProducts";
import { formatCurrency } from "@/lib/formatters";
import { format } from "date-fns";
import {
    LineChart,
    Line,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
} from "recharts";
import { Loader2 } from "lucide-react";

interface Props {
    masterId: string;
}

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

export function PriceHistoryChart({ masterId }: Props) {
    const { data, isLoading } = usePriceHistory(masterId);

    if (isLoading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin" /></div>;
    if (!data || data.length === 0) return <div className="text-center p-10 text-slate-500">No history data available yet.</div>;


    const chartData = data.reduce<ChartDataPoint[]>((acc, curr) => {
        const dateKey = format(new Date(curr.date), "d MMM");

        const existingEntry = acc.find((e) => e.date === dateKey);

        if (existingEntry) {
            existingEntry[curr.storeName] = curr.price;
        } else {
            acc.push({ date: dateKey, [curr.storeName]: curr.price });
        }
        return acc;
    }, []);
    const stores = Array.from(new Set(data.map(d => d.storeName)));

    return (
        <div className="h-100 w-full bg-white p-4 rounded-xl border border-slate-200 shadow-sm flex flex-col">

            <h3 className="text-lg font-semibold mb-8 text-slate-800 shrink-0">
                Price Trend History
            </h3>

            <div className="flex-1 w-full min-h-0">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={chartData} margin={{ top: 10, right: 30, left: 20, bottom: 15 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />

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
                            domain={[(dataMin: number) => Math.floor(dataMin * 0.9), 'auto']}
                        />

                        <Tooltip
                            itemSorter={(item) => item.value as number}
                            contentStyle={{
                                borderRadius: "12px",
                                border: "none",
                                boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)"
                            }}
                            formatter={(value: number | undefined, name: string | undefined) => [
                                formatCurrency(value ?? 0),
                                name ?? "Unknown Store"
                            ]}
                        />

                        <Legend verticalAlign="bottom" wrapperStyle={{ paddingTop: "10px" }} iconType="circle" />

                        {stores.map((store) => (
                            <Line
                                key={store}
                                type="monotone"
                                dataKey={store}
                                stroke={STORE_COLORS[store] || STORE_COLORS["default"]}
                                strokeWidth={3}
                                strokeOpacity={0.8}
                                dot={{ r: 4, strokeWidth: 2, fill: "#fff" }}
                                activeDot={{ r: 6, strokeWidth: 0 }}
                                connectNulls={true}
                            />
                        ))}
                    </LineChart>
                </ResponsiveContainer>
            </div>
        </div>
    );
}