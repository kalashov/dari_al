import Link from "next/link";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

export default function Home() {
  return (
    <main className="min-h-screen">
      {/* Hero секция */}
      <section className="bg-gradient-to-r from-primary/10 to-primary/5 py-20">
        <div className="container mx-auto px-4">
          <div className="max-w-3xl mx-auto text-center">
            <h1 className="text-5xl font-bold mb-6 text-foreground">
              Dari al
            </h1>
            <p className="text-xl text-muted-foreground mb-8">
              Широкий ассортимент лекарственных препаратов и медицинских товаров
              с доставкой на дом
            </p>
            <div className="flex gap-4 justify-center">
              <Button size="lg" asChild>
                <Link href="/catalog">Перейти в каталог</Link>
              </Button>
              <Button size="lg" variant="outline" asChild>
                <Link href="/delivery">Условия доставки</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>

      {/* Преимущества */}
      <section className="py-16 bg-background">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">
            Почему выбирают нас
          </h2>
          <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Быстрая доставка</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Доставляем заказы в течение дня по всему городу
                </p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>Широкий ассортимент</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Более 10 000 наименований лекарств и медицинских товаров
                </p>
              </CardContent>
            </Card>
            <Card>
              <CardHeader>
                <CardTitle>Гарантия качества</CardTitle>
              </CardHeader>
              <CardContent>
                <p className="text-muted-foreground">
                  Все товары сертифицированы и имеют необходимые лицензии
                </p>
              </CardContent>
            </Card>
          </div>
        </div>
      </section>

      {/* Как заказать */}
      <section className="py-16 bg-muted/50">
        <div className="container mx-auto px-4">
          <h2 className="text-3xl font-bold text-center mb-12">
            Как сделать заказ
          </h2>
          <div className="max-w-4xl mx-auto">
            <div className="grid grid-cols-1 md:grid-cols-4 gap-6">
              <div className="text-center">
                <div className="w-16 h-16 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                  1
                </div>
                <h3 className="font-semibold mb-2">Выберите товары</h3>
                <p className="text-sm text-muted-foreground">
                  Найдите нужные препараты в каталоге
                </p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                  2
                </div>
                <h3 className="font-semibold mb-2">Добавьте в корзину</h3>
                <p className="text-sm text-muted-foreground">
                  Выберите количество и добавьте товары
                </p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                  3
                </div>
                <h3 className="font-semibold mb-2">Оформите заказ</h3>
                <p className="text-sm text-muted-foreground">
                  Укажите адрес и контактные данные
                </p>
              </div>
              <div className="text-center">
                <div className="w-16 h-16 rounded-full bg-primary text-primary-foreground flex items-center justify-center text-2xl font-bold mx-auto mb-4">
                  4
                </div>
                <h3 className="font-semibold mb-2">Получите заказ</h3>
                <p className="text-sm text-muted-foreground">
                  Мы доставим в удобное для вас время
                </p>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* Контакты */}
      <section className="py-16 bg-background">
        <div className="container mx-auto px-4">
          <div className="max-w-2xl mx-auto text-center">
            <h2 className="text-3xl font-bold mb-6">Свяжитесь с нами</h2>
            <p className="text-muted-foreground mb-6">
              Есть вопросы? Мы всегда готовы помочь!
            </p>
            <div className="flex gap-4 justify-center">
              <Button variant="outline" asChild>
                <a href="tel:+77777777777">+7 777 777 77 77</a>
              </Button>
              <Button variant="outline" asChild>
                <Link href="/contacts">Контакты</Link>
              </Button>
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
