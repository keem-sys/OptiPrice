import { Link } from "react-router-dom";
import { ShoppingBasket } from "lucide-react";
import { SiGithub } from "react-icons/si";

export function Footer() {
    return (
        <footer className="border-t border-slate-200 bg-white mt-auto">
            <div className="container mx-auto px-4 py-12">

                {/* Top Section: Grid Layout */}
                <div className="grid grid-cols-1 md:grid-cols-3 gap-8 mb-8">

                    {/* Column 1: Brand & Disclaimer (Crucial for your Capstone) */}
                    <div className="flex flex-col gap-4">
                        <div className="flex items-center gap-2 text-slate-900">
                            <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600 text-white shadow-sm">
                                <ShoppingBasket size={18} />
                            </div>
                            <span className="font-bold text-lg">OptiPrice</span>
                        </div>
                        <p className="text-sm text-slate-500 leading-relaxed">
                            Your smart grocery companion. Compare prices across South Africa's top retailers and never overpay for daily essentials again.
                        </p>
                        {/* THE ACADEMIC/LEGAL DISCLAIMER */}
                        <p className="text-xs text-slate-400 italic mt-2 border-l-2 border-slate-200 pl-2">
                            * Educational project only. OptiPrice is not affiliated with, endorsed, or sponsored by Checkers, Pick n Pay, or Shoprite.
                        </p>
                    </div>

                    {/* Column 2: Quick Links */}
                    <div>
                        <h3 className="font-semibold text-slate-900 mb-4">Quick Links</h3>
                        <ul className="flex flex-col gap-3 text-sm text-slate-500">
                            <li><Link to="/" className="hover:text-indigo-600 transition-colors">Search Products</Link></li>
                            <li><Link to="/deals" className="hover:text-indigo-600 transition-colors">Daily Deals</Link></li>
                            <li><Link to="/history" className="hover:text-indigo-600 transition-colors">Price Trends</Link></li>
                        </ul>
                    </div>

                    {/* Column 3: Connect / Legal */}
                    <div>
                        <h3 className="font-semibold text-slate-900 mb-4">Project</h3>
                        <ul className="flex flex-col gap-3 text-sm text-slate-500">
                            <li><a href="#" className="hover:text-indigo-600 transition-colors">About the Project</a></li>
                            <li><a href="#" className="hover:text-indigo-600 transition-colors">Privacy Policy</a></li>
                            <li>
                                <a
                                    href="https://github.com/keem-sys/OptiPrice"
                                    target="_blank"
                                    rel="noreferrer"
                                    className="flex items-center gap-2 hover:text-indigo-600 transition-colors mt-2"
                                >
                                    <SiGithub size={16} />
                                    <span>View Source Code</span>
                                </a>
                            </li>
                        </ul>
                    </div>

                </div>

                {/* Bottom Section: Copyright Bar */}
                <div className="flex flex-col md:flex-row items-center justify-between pt-8 border-t border-slate-100 text-sm text-slate-400 gap-4">
                    <div>
                        &copy; {new Date().getFullYear()} OptiPrice. All rights reserved.
                    </div>
                    <div className="flex gap-4">
                        <span>Made with ❤️ in South Africa</span>
                    </div>
                </div>

            </div>
        </footer>
    );
}