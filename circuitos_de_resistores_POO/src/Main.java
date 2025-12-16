import java.util.Scanner;
public class Main {

    public static void main(String[] args) {
      Scanner sc= new Scanner(System.in);
      circuito circuito_atual = null;

     int opcao;

      do {

          System.out.println("\n=== MENU CIRCUITOS ===");
          System.out.println("1. Criar resistor simples");
          System.out.println("2. Criar circuito em série");
          System.out.println("3. Criar circuito em paralelo");
          System.out.println("4. Ver resistência equivalente");
          System.out.println("0. Sair");
          System.out.print("Escolha: ");
          opcao = sc.nextInt();

          switch (opcao) {
            case 1:
              System.out.print("Digite o valor da resistencia (ohms): ");
              double r= sc.nextDouble();
              circuito_atual = new resistor(r);

              System.out.println("Resistor Criado:" +  circuito_atual);
              break;
              case 2:
                  serie serie = new serie();
                  System.out.print("Quantos resistores deseja adicionar em série? ");
                  int n = sc.nextInt();
                  for (int i = 0; i < n; i++) {
                      System.out.print("Resistor " + (i+1) + " (Ohms): ");
                      double R = sc.nextDouble();
                      serie.add(new resistor(R));
                  }
                  circuito_atual = serie;
                  System.out.println("Circuito em série criado: " + circuito_atual);
                  break;
              case 3:
                  paralelo P = new paralelo();
                  System.out.print("quantos resistores deseja adicionar em paralelo ?");
                  int J= sc.nextInt();
                  for(int i=0; i<J; i++){
                      System.out.print("resistor " + (i+1) + "(ohns): ");
                      double R = sc.nextDouble();
                      P.add(new resistor(R));
                  }
                  circuito_atual = P;
                  System.out.println("circuito em paralelo Criado:" +  circuito_atual);
                break;
                case 4:
                    if(circuito_atual != null){

                        System.out.println("resistencia equivalente: " + circuito_atual.getresistencia());

                    }
                    else{
                        System.out.println("Nenhum circuito criado ainda!");
                    }
                    break;
              case 0:
                  System.out.println("Saindo...");
                  break;

              default: System.out.println("Opção inválida!");
          }
      }while(opcao!=0);
    sc.close();
    }
}