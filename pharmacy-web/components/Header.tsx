import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";

export function Header() {
  return (
    <header className="border-b bg-background sticky top-0 z-50">
      {/* Первый хедер */}
      <div className="border-b">
        <div className="container mx-auto px-4">
          <div className="flex items-center justify-between h-16">
            {/* Логотип слева */}
            <div className="flex items-center">
              <Link href="/" className="flex items-center">
                <span className="text-xl font-bold text-primary">Аптека Онлайн</span>
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
