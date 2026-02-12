import { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { ShoppingBasket, Menu, Heart, Search } from "lucide-react";
import { SiGithub } from "react-icons/si";
import { Button } from "@/components/ui/button";
import {
    Sheet,
    SheetContent,
    SheetTrigger,
    SheetHeader,
    SheetTitle,
    SheetDescription
} from "@/components/ui/sheet";
import { cn } from "@/lib/utils";

export function Navbar() {
    const [isOpen, setIsOpen] = useState(false);
    const location = useLocation();

    const navLinks = [
        { href: "/", label: "Home" },
        { href: "/deals", label: "Daily Deals" },
        { href: "/history", label: "Price Trends" },
    ];

    const isActive = (path: string) => location.pathname === path;

    return (
        <header className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/80 backdrop-blur-md">
            <div className="container mx-auto flex h-16 items-center justify-between px-4">

                {/* --- LEFT SIDE: MOBILE MENU & LOGO --- */}
                <div className="flex items-center gap-2">

                    {/* Mobile Menu (Sheet) */}
                    <Sheet open={isOpen} onOpenChange={setIsOpen}>
                        <SheetTrigger asChild>
                            <Button variant="ghost" size="icon" className="md:hidden mr-2">
                                <Menu className="h-5 w-5" />
                                <span className="sr-only">Toggle menu</span>
                            </Button>
                        </SheetTrigger>

                        <SheetContent side="left" className="w-75 sm:w-100">

                            <SheetHeader>
                                <SheetTitle className="text-left flex items-center gap-2">
                                    <ShoppingBasket className="h-5 w-5 text-indigo-600" />
                                    OptiPrice Menu
                                </SheetTitle>

                                <SheetDescription className="sr-only">
                                    Navigate through OptiPrice pages including Home, Deals, and Trends.
                                </SheetDescription>
                            </SheetHeader>

                            <div className="flex flex-col gap-6 mt-6">
                                <nav className="flex flex-col gap-2">
                                    {navLinks.map((link) => (
                                        <Link
                                            key={link.href}
                                            to={link.href}
                                            onClick={() => setIsOpen(false)}
                                            className={cn(
                                                "px-4 py-3 text-lg font-medium transition-colors rounded-md hover:bg-slate-100",
                                                isActive(link.href)
                                                    ? "text-indigo-600 bg-indigo-50"
                                                    : "text-slate-600"
                                            )}
                                        >
                                            {link.label}
                                        </Link>
                                    ))}
                                </nav>
                            </div>
                        </SheetContent>
                    </Sheet>

                    {/* Desktop Logo */}
                    <Link to="/" className="flex items-center gap-2 group">
                        <div className="flex h-9 w-9 items-center justify-center rounded-lg bg-indigo-600 text-white shadow-md shadow-indigo-200 transition-transform group-hover:scale-105">
                            <ShoppingBasket size={20} />
                        </div>
                        <span className="hidden font-bold tracking-tight text-slate-900 sm:inline-block text-xl">
                            OptiPrice
                        </span>
                    </Link>
                </div>

                {/* DESKTOP NAVIGATION */}
                <nav className="hidden md:flex items-center gap-8 text-sm font-medium">
                    {navLinks.map((link) => (
                        <Link
                            key={link.href}
                            to={link.href}
                            className={cn(
                                "transition-colors hover:text-indigo-600",
                                isActive(link.href) ? "text-indigo-600 font-bold" : "text-slate-600"
                            )}
                        >
                            {link.label}
                        </Link>
                    ))}
                </nav>

                {/* RIGHT SIDE: ACTIONS */}
                <div className="flex items-center gap-1 sm:gap-2">

                    {/* Search Icon */}
                    <Button variant="ghost" size="icon" className="text-slate-500 hover:text-indigo-600">
                        <Search size={20} />
                        <span className="sr-only">Search products</span>
                    </Button>

                    {/* Watchlist */}
                    <Button variant="ghost" size="icon" className="text-slate-500 hover:text-red-500 hover:bg-red-50">
                        <Heart size={20} />
                        <span className="sr-only">Watchlist</span>
                    </Button>

                    {/* GitHub Link */}
                    <Button variant="ghost" size="icon" asChild className="text-slate-500 hover:text-slate-900">
                        <a
                            href="https://github.com/keem-sys/"
                            target="_blank"
                            rel="noreferrer"
                        >
                            <SiGithub size={20} />
                            <span className="sr-only">GitHub</span>
                        </a>
                    </Button>

                </div>
            </div>
        </header>
    );
}