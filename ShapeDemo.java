import java.util.Scanner;
abstract class Shape{
    float dim1,dim2;
    Shape(float d1,float d2){
        dim1=d1;
        dim2=d2;
    }
    abstract void printArea();
}
class Rectangle extends Shape{
    Rectangle(float length,float breadth){
        super(length,breadth);
    }
    void printArea(){
        float area=dim1*dim2;
        System.out.println("Area of Rectangle:"+area);
    }
}
class Triangle extends Shape{
    Triangle(float base,float height){
        super(base,height);
    }
    void printArea(){
        float area=0.5f*dim1*dim2;
        System.out.println("Area of Triangle:"+area);
    }
}
class Circle extends Shape{
    Circle(float radius){
        super(radius,0);
    }
    void printArea(){
        float area=3.14159f*dim1*dim2;
        System.out.println("Area of Circle:"+area);
    }
}
public class Shape{
    public static void main(String[] args){
        Scanner sc=new Scanner(System.in);
        System.out.print("Enter length of Rectangle:");
        int l=sc.nextInt();
        System.out.println("Enter breadth of Retangle:");
        int b=sc.nextInt();
        Rectangle r=new Rectangle(l,b);
        r.printArea();
        System.out.print("Enter base of Triangle:");
        int base=sc.nextInt();
        System.out.print("Enter height of Triangle:");
        int h=sc.nextInt();
        Triangle t=new Triangle(base,h);
        t.printArea();
        System.out.print("Enter radius of circle:");
        int rad=sc.nextInt();
        Circle c=new Circle(rad);
        c.printArea();
        sc.close();
    }
}
