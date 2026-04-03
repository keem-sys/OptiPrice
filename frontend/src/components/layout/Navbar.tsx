import React, {useEffect, useRef, useState} from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import { ShoppingBasket, Menu, Heart, Search, X } from "lucide-react";
import { SiGithub } from "react-icons/si";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
    Sheet,
    SheetContent,
    SheetTrigger,
    SheetHeader,
    SheetTitle,
    SheetDescription
} from "@/components/ui/sheet";
import { cn } from "@/lib/utils";
import {toast} from "sonner";

export function Navbar() {
    const [isOpen, setIsOpen] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);
    const[searchQuery, setSearchQuery] = useState("");

    const location = useLocation();
    const navigate = useNavigate();

    const searchContainerRef = useRef<HTMLDivElement>(null);

    const navLinks = [
        { href: "/", label: "Home" },
        { href: "/deals", label: "Daily Deals" },
        { href: "/trends", label: "Price Trends" },
    ];

    const handleWatchlistClick = () => {
        toast.info("Watchlist Feature Coming Soon!", {
            duration: 2500,
        });
    };

    const isActive = (path: string) => location.pathname === path;
    const isHomePage = location.pathname === "/";

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape" && isSearchOpen) {
                setIsSearchOpen(false);
            }
        };

        const handleClickOutside = (e: MouseEvent) => {
            if (isSearchOpen && searchContainerRef.current && !searchContainerRef.current.contains(e.target as Node)) {
                setIsSearchOpen(false);
            }
        };

        document.addEventListener("keydown", handleKeyDown);
        document.addEventListener("mousedown", handleClickOutside);

        return () => {
            document.removeEventListener("keydown", handleKeyDown);
            document.removeEventListener("mousedown", handleClickOutside);
        };
    }, [isSearchOpen]);

    const handleGlobalSearch = (e: React.SyntheticEvent) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/?q=${encodeURIComponent(searchQuery.trim())}`);
            setIsSearchOpen(false);
            setSearchQuery("");
        }
    };

    return (
        <header ref={searchContainerRef} className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/95 backdrop-blur-md">
            <div className="container mx-auto flex h-16 items-center justify-between px-4 relative">

                {/* LEFT SIDE: MOBILE MENU & LOGO */}
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

                    {/* Global Search Toggle Icon */}
                    {!isHomePage && (
                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => setIsSearchOpen(!isSearchOpen)}
                            className={cn("text-slate-500 hover:text-indigo-600", isSearchOpen && "bg-slate-100 text-indigo-600")}
                        >
                            {isSearchOpen ? <X size={20} /> : <Search size={20} />}
                            <span className="sr-only">Toggle Global Search</span>
                        </Button>
                    )}


                    {/* Watchlist */}
                    <Button variant="ghost" size="icon"  onClick={handleWatchlistClick} className="text-slate-500 hover:text-red-500 hover:bg-red-50 cursor-pointer">
                        <Heart size={20} />
                        <span className="sr-only">Watchlist</span>
                    </Button>

                    {/* GitHub Link */}
                    <Button variant="ghost" size="icon" asChild className="text-slate-500 hover:text-slate-900">
                        <a
                            href="https://github.com/keem-sys/OptiPrice"
                            target="_blank"
                            rel="noreferrer"
                        >
                            <SiGithub size={20} />
                            <span className="sr-only">GitHub</span>
                        </a>
                    </Button>
                </div>
            </div>

            {/* GLOBAL SEARCH DROPDOWN */}
            {isSearchOpen && !isHomePage && (
                <div className="absolute top-full left-0 w-full border-b border-slate-200 bg-white shadow-xl animate-in slide-in-from-top-2 p-4">
                    <div className="container mx-auto max-w-3xl">
                        <form onSubmit={handleGlobalSearch} className="relative flex items-center">
                            <Search className="absolute left-4 h-5 w-5 text-slate-400" />
                            <Input
                                type="text"
                                autoFocus
                                placeholder="Search for groceries..."
                                className="h-12 w-full rounded-full border-slate-200 bg-slate-50 pl-12 pr-24 text-lg focus-visible:ring-indigo-500"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                            <Button
                                size="sm"
                                type="submit"
                                className="absolute right-2 rounded-full bg-indigo-600 hover:bg-indigo-700"
                            >
                                Search
                            </Button>
                        </form>
                    </div>
                </div>
            )}
        </header>
    );
}