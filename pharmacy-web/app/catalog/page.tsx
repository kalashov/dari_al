"use client";

import { useState } from "react";
import { Input } from "@/components/ui/input";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { ProductCard } from "@/components/ProductCard";

// Моковые данные товаров
const mockProducts = [
  {
    id: "1",
    name: "УТРОЖЕСТАН 200 мг №14 капс Прогестерон",
    price: 12500,
    imageUrl: undefined,
  },
  {
    id: "2",
    name: "НЕФРОСТЕН №60 таб Комплекс",
    price: 8900,
    imageUrl: undefined,
  },
  {
    id: "3",
    name: "DENTINALE NATURA 20 мл гель для десен Комплекс",
    price: 3200,
    imageUrl: undefined,
  },
  {
    id: "4",
    name: "FDP 5 г №1фл Фосфруктоза",
    price: 4500,
    imageUrl: undefined,
  },
  {
    id: "5",
    name: "Парацетамол 500 мг №20 таблетки",
    price: 1200,
    imageUrl: undefined,
  },
  {
    id: "6",
    name: "Ибупрофен 400 мг №20 таблетки",
    price: 1500,
    imageUrl: undefined,
  },
  {
    id: "7",
    name: "Аспирин Кардио 100 мг №30 таблетки",
    price: 2800,
    imageUrl: undefined,
  },
  {
    id: "8",
    name: "Амоксициллин 500 мг №16 капсулы",
    price: 3500,
    imageUrl: undefined,
  },
  {
    id: "9",
    name: "Витамин D3 2000 МЕ №60 капсулы",
    price: 4200,
    imageUrl: undefined,
  },
  {
    id: "10",
    name: "Омега-3 1000 мг №60 капсулы",
    price: 5500,
    imageUrl: undefined,
  },
  {
    id: "11",
    name: "Магний B6 №60 таблетки",
    price: 3800,
    imageUrl: undefined,
  },
  {
    id: "12",
    name: "Цинк 15 мг №30 таблетки",
    price: 2100,
    imageUrl: undefined,
  },
];

export default function CatalogPage() {
  const [minPrice, setMinPrice] = useState("");
  const [maxPrice, setMaxPrice] = useState("");

  return (
    <main className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-6">
        <h1 className="text-3xl font-bold mb-6">Каталог товаров</h1>

        <div className="flex gap-6">
          {/* Боковая панель с фильтрами */}
          <aside className="w-64 shrink-0">
            <Card>
              <CardHeader>
                <CardTitle>Фильтры</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div>
                  <label className="text-sm font-medium mb-2 block">Цена</label>
                  <div className="space-y-2">
                    <div>
                      <Input
                        type="number"
                        placeholder="От"
                        value={minPrice}
                        onChange={(e) => setMinPrice(e.target.value)}
                      />
                    </div>
                    <div>
                      <Input
                        type="number"
                        placeholder="До"
                        value={maxPrice}
                        onChange={(e) => setMaxPrice(e.target.value)}
                      />
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </aside>

          {/* Основной контент с товарами */}
          <div className="flex-1">
            <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-4 gap-4">
              {mockProducts.map((product) => (
                <ProductCard
                  key={product.id}
                  id={product.id}
                  name={product.name}
                  price={product.price}
                  imageUrl={product.imageUrl}
                />
              ))}
            </div>
          </div>
        </div>
      </div>
    </main>
  );
}
