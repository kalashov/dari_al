"use client";

import { useEffect, useState } from "react";
import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function Header() {
  const [isScrolledDown, setIsScrolledDown] = useState(false);
  const [lastScrollY, setLastScrollY] = useState(0);
  const [isMounted, setIsMounted] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  useEffect(() => {
    if (!isMounted) return;

    const handleScroll = () => {
      const currentScrollY = window.scrollY;
      
      // Хедер скрывается при скролле вниз (после 100px)
      // Хедер появляется только когда доскроллили до самого верха (<= 10px)
      if (currentScrollY <= 10) {
        // В самом верху страницы - показываем хедер
        setIsScrolledDown(false);
      } else if (currentScrollY > 100) {
        // Проскроллили больше 100px - скрываем хедер
        setIsScrolledDown(true);
      }
      
      setLastScrollY(currentScrollY);
    };

    window.addEventListener("scroll", handleScroll, { passive: true });
    return () => window.removeEventListener("scroll", handleScroll);
  }, [lastScrollY, isMounted]);

  // Используем isMounted для предотвращения ошибок гидратации
  // Хедер скрыт если мы не в самом верху страницы
  const shouldHide = isMounted && isScrolledDown;

  return (
    <header className="border-b bg-background sticky top-0 z-50">
      {/* Первый хедер */}
      <div
        className={`border-b overflow-hidden transition-all duration-300 ease-in-out ${
          shouldHide ? "max-h-0 opacity-0" : "max-h-16 opacity-100"
        }`}
        suppressHydrationWarning
      >
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            {/* Логотип слева */}
            <div className="flex items-center">
              <Link href="/" className="flex items-center">
                <span className="text-xl font-bold text-primary">Dari al</span>
              </Link>
            </div>

            {/* Навигационные разделы */}
            <nav className="flex items-center gap-6">
              <Button variant="ghost" asChild>
                <Link href="/about">О нас</Link>
              </Button>
              <Button variant="ghost" asChild>
                <Link href="/delivery">Доставка</Link>
              </Button>
              <Button variant="ghost" asChild>
                <Link href="/contacts">Контакты</Link>
              </Button>
            </nav>

            {/* Номер телефона справа */}
            <div className="flex items-center">
              <a
                href="tel:+77777777777"
                className="text-sm font-medium text-foreground hover:text-primary transition-colors"
              >
                +7 777 777 77 77
              </a>
            </div>
          </div>
        </div>
      </div>

      {/* Второй хедер */}
      <div className="container mx-auto px-4">
        <div className="flex items-center justify-between h-14 gap-4">
          {/* Кнопка каталога и поиск */}
          <div className="flex items-center gap-3 flex-1 max-w-2xl">
            <Button variant="default" asChild>
              <Link href="/catalog">Каталог товаров</Link>
            </Button>
            <div className="flex-1">
              <Input
                type="search"
                placeholder="Поиск товаров..."
                className="w-full"
              />
            </div>
          </div>

          {/* Кнопки справа */}
          <div className="flex items-center gap-3">
            <Button variant="outline" asChild>
              <Link href="/login">Вход</Link>
            </Button>
            <Button variant="outline" asChild>
              <Link href="/cart">Корзина</Link>
            </Button>
          </div>
        </div>
      </div>
    </header>
  );
}
