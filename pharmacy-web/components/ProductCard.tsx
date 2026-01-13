import { Card, CardContent } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { ShoppingCart } from "lucide-react";
import Image from "next/image";

interface ProductCardProps {
  id: string;
  name: string;
  price: number;
  imageUrl?: string;
}

export function ProductCard({ id, name, price, imageUrl }: ProductCardProps) {
  return (
    <Card className="overflow-hidden hover:shadow-md transition-shadow py-0 gap-0">
      <div className="relative w-full aspect-square bg-gray-100">
        {imageUrl ? (
          <Image
            src={imageUrl}
            alt={name}
            fill
            className="object-cover"
          />
        ) : (
          <div className="w-full h-full flex items-center justify-center text-gray-400">
            <span>Нет фото</span>
          </div>
        )}
      </div>
      <CardContent className="p-4 space-y-3">
        <h3 className="font-medium text-sm line-clamp-2 min-h-[2.5rem]">
          {name}
        </h3>
        <div className="flex items-center justify-between">
          <span className="text-lg font-semibold">{price.toLocaleString()} ₸</span>
          <Button size="sm" className="gap-2">
            <ShoppingCart className="size-4" />
            В корзину
          </Button>
        </div>
      </CardContent>
    </Card>
  );
}

