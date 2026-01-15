import Link from "next/link";
import { Button } from "@/components/ui/button";

export function Header() {
  return (
    <header className="border-b bg-background sticky top-0 z-50">
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
    </header>
  );
}
