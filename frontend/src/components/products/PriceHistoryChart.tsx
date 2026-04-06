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

const CustomLegend = ({ payload }: any) => {
    return (
        <div className="flex flex-wrap justify-center gap-x-6 gap-y-3 pt-4">
            {payload.map((entry: any, index: number) => (
                <div key={`item-${index}`} className="flex items-center gap-2">
                    <span
                        className="w-3 h-3 rounded-full shadow-sm"
                        style={{ backgroundColor: entry.color }}
                    />
                    <span className="text-sm font-medium text-slate-700">
                        {entry.value}
                    </span>
                </div>
            ))}
        </div>
    );
};

export function PriceHistoryChart({ masterId }: Props) {
    const { data, isLoading } = usePriceHistory(masterId);

    if (isLoading) return <div className="flex justify-center p-10"><Loader2 className="animate-spin text-indigo-600" /></div>;
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
    },[]);

    const stores = Array.from(new Set(data.map(d => d.storeName)));

    return (
        <div className="w-full bg-white p-4 sm:p-6 rounded-xl border border-slate-200 shadow-sm flex flex-col">
            <h3 className="text-lg font-semibold mb-6 text-slate-800 shrink-0">
                Price Trend History
            </h3>

            <div className="w-full h-75 sm:h-87.5 min-h-75">
                <ResponsiveContainer width="100%" height="100%">
                    <LineChart data={chartData} margin={{ top: 5, right: 10, left: 0, bottom: 5 }}>
                        <CartesianGrid strokeDasharray="3 3" stroke="#e2e8f0" vertical={false} />

                        <XAxis
                            dataKey="date"
                            stroke="#94a3b8"
                            fontSize={12}
                            tickMargin={12}
                            axisLine={false}
                            tickLine={false}
                        />
                        <YAxis
                            stroke="#94a3b8"
                            fontSize={12}
                            width={55}
                            tickFormatter={(val) => `R${Number(val).toFixed(2)}`}
                            axisLine={false}
                            tickLine={false}
                            domain={[(dataMin: number) => Math.floor(dataMin * 0.9), 'auto']}
                        />

                        <Tooltip
                            itemSorter={(item) => item.value as number}
                            contentStyle={{
                                borderRadius: "12px",
                                border: "1px solid #e2e8f0",
                                boxShadow: "0 10px 15px -3px rgb(0 0 0 / 0.1)"
                            }}
                            formatter={(value: number | undefined, name: string | undefined) =>[
                                formatCurrency(value ?? 0),
                                name ?? "Unknown Store"
                            ]}
                        />

                        <Legend
                            content={<CustomLegend />}
                            verticalAlign="bottom"
                        />

                        {stores.map((store) => (
                            <Line
                                key={store}
                                type="monotone"
                                dataKey={store}
                                stroke={STORE_COLORS[store] || STORE_COLORS["default"]}
                                strokeWidth={3}
                                strokeOpacity={0.9}
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