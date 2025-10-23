package calculator;

import java.util.Scanner;

public class calc
{
    public static void main(String[] args) {

        System.out.println("Calculator!");

        try {
            Thread.sleep(3000);
        }catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        Scanner sc = new Scanner(System.in);

        System.out.println("Enter the first number : ");
        double a = sc.nextDouble();

        System.out.println("Enter the second number : ");
        double b = sc.nextDouble();

        System.out.println("Select the operator +, -, *, /, %");
        char operator = sc.next().charAt(0);

        double result = 0;

        switch (operator)
        {
            case '+' ->
                result = a+b;

            case '-' ->
                result = a-b;

            case '*' ->
                result = a*b;

            case '/' -> {
                if(b == 0)
                {
                    throw new ArithmeticException("Number cannot be divided by zero!");
                }
                result = a/b;
            }

            case '%' ->
                result = a%b;

            default ->
                System.out.println("Invalid operator!!");
        }

        System.out.println("Result => " + result);

    }
}
