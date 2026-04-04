import React, { useEffect, useRef, useState } from "react";
import { Link, useLocation, useNavigate } from "react-router-dom";
import {
    ShoppingBasket,
    Menu,
    Heart,
    Search,
    X,
    Home,
    Tag,
    TrendingUp,
    ChevronRight
} from "lucide-react";
import { SiGithub } from "react-icons/si";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import {
    Sheet,
    SheetContent,
    SheetTrigger,
    SheetHeader,
    SheetTitle,
    SheetDescription,
    SheetFooter,
    SheetClose
} from "@/components/ui/sheet";
import { cn } from "@/lib/utils";
import { toast } from "sonner";

const navLinks = [
    { href: "/", label: "Home", icon: Home },
    { href: "/deals", label: "Daily Deals", icon: Tag },
    { href: "/trends", label: "Price Trends", icon: TrendingUp },
];

export function Navbar() {
    const [isOpen, setIsOpen] = useState(false);
    const [isSearchOpen, setIsSearchOpen] = useState(false);
    const [searchQuery, setSearchQuery] = useState("");

    const location = useLocation();
    const navigate = useNavigate();
    const searchContainerRef = useRef<HTMLDivElement>(null);

    const isActive = (path: string) => location.pathname === path;
    const isHomePage = location.pathname === "/";

    const handleWatchlistClick = () => {
        toast.info("Watchlist Feature Coming Soon!", { duration: 2500 });
        setIsOpen(false);
    };

    const handleGlobalSearch = (e: React.SyntheticEvent) => {
        e.preventDefault();
        if (searchQuery.trim()) {
            navigate(`/?q=${encodeURIComponent(searchQuery.trim())}`);
            setIsSearchOpen(false);
            setSearchQuery("");
        }
    };

    useEffect(() => {
        const handleKeyDown = (e: KeyboardEvent) => {
            if (e.key === "Escape" && isSearchOpen) setIsSearchOpen(false);
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

    return (
        <header ref={searchContainerRef} className="sticky top-0 z-50 w-full border-b border-slate-200 bg-white/95 backdrop-blur-md shadow-sm">
            <div className="container mx-auto flex h-16 items-center justify-between px-4 lg:px-8 relative">

                {/* LEFT: BRAND LOGO */}
                <Link to="/" className="flex items-center gap-2 group z-10">
                    <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-indigo-600 text-white shadow-md shadow-indigo-200 transition-transform group-hover:scale-105">
                        <ShoppingBasket size={20} />
                    </div>
                    <span className="font-bold tracking-tight text-slate-900 text-lg sm:text-xl">
                        OptiPrice
                    </span>
                </Link>

                {/* CENTER: DESKTOP NAVIGATION */}
                <nav className="hidden md:flex items-center gap-1 ml-6"> {/* added ml-6 to space from logo */}
                    {navLinks.map((link) => (
                        <Link
                            key={link.href}
                            to={link.href}
                            className={cn(
                                "px-4 py-2 rounded-full text-sm font-medium transition-all",
                                isActive(link.href)
                                    ? "bg-indigo-50 text-indigo-600 shadow-sm"
                                    : "text-slate-600 hover:text-slate-900 hover:bg-slate-100"
                            )}
                        >
                            {link.label}
                        </Link>
                    ))}
                </nav>

                {/* RIGHT SIDE: ACTIONS & MOBILE MENU */}
                <div className="flex items-center gap-1 sm:gap-2 z-10">

                    {/* Global Search Toggle */}
                    {!isHomePage && (
                        <Button
                            variant="ghost"
                            size="icon"
                            onClick={() => setIsSearchOpen(!isSearchOpen)}
                            className={cn(
                                "min-h-11 min-w-11 rounded-full transition-colors",
                                isSearchOpen ? "bg-indigo-50 text-indigo-600" : "text-slate-500 hover:bg-slate-100"
                            )}
                        >
                            {isSearchOpen ? <X size={20} /> : <Search size={20} />}
                            <span className="sr-only">Toggle Search</span>
                        </Button>
                    )}

                    <div className="hidden sm:flex items-center gap-1">
                        <Button variant="ghost" size="icon" onClick={handleWatchlistClick} className="rounded-full text-slate-500 hover:text-red-500 hover:bg-red-50">
                            <Heart size={20} />
                            <span className="sr-only">Watchlist</span>
                        </Button>

                        <Button variant="ghost" size="icon" asChild className="rounded-full text-slate-500 hover:text-slate-900 hover:bg-slate-100">
                            <a href="https://github.com/keem-sys/OptiPrice" target="_blank" rel="noreferrer">
                                <SiGithub size={18} />
                                <span className="sr-only">GitHub</span>
                            </a>
                        </Button>
                    </div>

                    {/* MOBILE FULL-SCREEN MENU */}
                    <Sheet open={isOpen} onOpenChange={setIsOpen}>
                        <SheetTrigger asChild>
                            <button
                                    className="md:hidden min-h-11 min-w-11 ml-1 rounded-full text-slate-700 hover:bg-slate-100">
                                <Menu className="h-6 w-6" />
                                <span className="sr-only">Toggle menu</span>
                            </button>
                        </SheetTrigger>


                        <SheetContent side="right" className="w-full max-w-full sm:max-w-sm flex flex-col p-0 [&>button]:hidden">

                            <div className="flex items-center justify-between p-4 border-b border-slate-100">
                                <SheetHeader className="text-left space-y-0">
                                    <SheetTitle className="flex items-center gap-2 text-xl m-0">
                                        <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-indigo-600 text-white">
                                            <ShoppingBasket size={18} />
                                        </div>
                                        OptiPrice
                                    </SheetTitle>
                                    <SheetDescription className="sr-only">
                                        Mobile Navigation Menu
                                    </SheetDescription>
                                </SheetHeader>

                                <SheetClose asChild>
                                    <button className="h-12 w-12 rounded-full text-slate-500 hover:bg-slate-100 hover:text-slate-900">
                                        <X className="h-7 w-7" />
                                        <span className="sr-only">Close menu</span>
                                    </button>
                                </SheetClose>
                            </div>

                            {/* MOBILE NAV LINKS */}
                            <div className="flex-1 overflow-y-auto py-6 px-4">
                                <nav className="flex flex-col gap-2">
                                    {navLinks.map((link) => {
                                        const Icon = link.icon;
                                        const active = isActive(link.href);
                                        return (
                                            <Link
                                                key={link.href}
                                                to={link.href}
                                                onClick={() => setIsOpen(false)}
                                                className={cn(
                                                    "group flex items-center justify-between px-4 py-4 text-lg font-medium transition-all rounded-2xl",
                                                    active
                                                        ? "text-indigo-700 bg-indigo-50 shadow-sm"
                                                        : "text-slate-600 hover:bg-slate-50 hover:text-slate-900"
                                                )}
                                            >
                                                <div className="flex items-center gap-4">
                                                    <Icon className={cn("h-6 w-6", active ? "text-indigo-600" : "text-slate-400 group-hover:text-slate-600")} />
                                                    {link.label}
                                                </div>
                                                {active && <ChevronRight className="h-5 w-5 text-indigo-400" />}
                                            </Link>
                                        );
                                    })}
                                </nav>
                            </div>

                            {/* MOBILE MENU FOOTER */}
                            <SheetFooter className="p-6 border-t border-slate-100 mt-auto flex-col sm:flex-col gap-3 bg-slate-50/50">
                                <Button
                                    variant="outline"
                                    className="w-full justify-start gap-3 h-14 rounded-xl text-base text-slate-600 hover:text-red-600 hover:bg-red-50 border-slate-200 bg-white shadow-sm"
                                    onClick={handleWatchlistClick}
                                >
                                    <Heart className="h-5 w-5" />
                                    My Watchlist
                                </Button>
                                <Button
                                    variant="ghost"
                                    className="w-full justify-start gap-3 h-14 rounded-xl text-base text-slate-600 bg-slate-100 hover:bg-slate-200"
                                    asChild
                                >
                                    <a href="https://github.com/keem-sys/OptiPrice" target="_blank" rel="noreferrer">
                                        <SiGithub className="h-5 w-5" />
                                        GitHub Repository
                                    </a>
                                </Button>
                            </SheetFooter>
                        </SheetContent>
                    </Sheet>
                </div>
            </div>

            {/* GLOBAL SEARCH DROPDOWN */}
            {isSearchOpen && !isHomePage && (
                <div className="absolute top-full left-0 w-full border-b border-slate-200 bg-white shadow-lg animate-in slide-in-from-top-2 p-3 sm:p-4">
                    <div className="container mx-auto max-w-3xl">
                        <form onSubmit={handleGlobalSearch} className="relative flex items-center">
                            <Search className="absolute left-4 h-5 w-5 text-slate-400" />
                            <Input
                                type="text"
                                autoFocus
                                placeholder="Search Shoprite, PnP, and Checkers..."
                                className="h-14 w-full rounded-2xl border-slate-200 bg-slate-50 pl-12 pr-28 text-base focus-visible:ring-indigo-500 focus-visible:bg-white transition-all shadow-inner"
                                value={searchQuery}
                                onChange={(e) => setSearchQuery(e.target.value)}
                            />
                            <Button
                                size="sm"
                                type="submit"
                                className="absolute right-2 h-10 px-4 rounded-xl bg-indigo-600 hover:bg-indigo-700 text-white font-medium shadow-sm"
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