import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
    public static void main(String[] args) {
        String nomeArquivoMasculino = "oscar_age_male.csv";
        String nomeArquivoFeminino = "oscar_age_female.csv";
        Scanner sc = new Scanner(System.in);
        Scanner sc2 = new Scanner(System.in);
        String escolhaUsuario = "";

        System.out.println("Bem-Vindo à pesquisa de prêmios do Oscar");
        while (!escolhaUsuario.equals("6")){
            System.out.println("1. Ator e Atriz mais jovem a ganhar um Oscar");
            System.out.println("2. Ator e Atriz mais premiados");
            System.out.println("3. Ator e Atriz mais premiados entre 20 e 30 anos");
            System.out.println("4. Atores e Atrizes que ganharam mais de um Oscar");
            System.out.println("5. Pesquisar ator ou atriz");
            System.out.println("6. Encerrar programa");
            System.out.print("Escolha uma opção: ");
            escolhaUsuario = sc.next();
            System.out.println();
            switch (escolhaUsuario){
                case "1":
                    achaMaisJovem(nomeArquivoMasculino);
                    System.out.println();
                    achaMaisJovem(nomeArquivoFeminino);
                    System.out.println();
                    break;
                case "2":
                    maiorVencedor(nomeArquivoMasculino);
                    System.out.println();
                    maiorVencedor(nomeArquivoFeminino);
                    System.out.println();
                    break;
                case "3":
                    maiorVencedorVinteTrintaAnos(nomeArquivoMasculino);
                    System.out.println();
                    maiorVencedorVinteTrintaAnos(nomeArquivoFeminino);
                    System.out.println();
                    break;
                case "4":
                    maioresVencedores(nomeArquivoMasculino, nomeArquivoFeminino);
                    System.out.println();
                    break;
                case "5":
                    System.out.print("Digite o nome do ator ou da atriz: ");
                    String nomeAtor = sc2.nextLine();
                    achaAtorPeloNome(nomeArquivoMasculino, nomeArquivoFeminino, nomeAtor);
                    System.out.println();
                    break;
                case "6":
                    break;
                default:
                    System.out.println("Escolha ínvalida. Tente novamente.");
                    System.out.println();
            }
        }
        System.out.println("Até mais.");
    }

    private static void achaMaisJovem(String nomeArquivo) {
        try (Stream<String> stream = Files.lines(Paths.get(nomeArquivo))) {
            List<String> lines = stream.collect(Collectors.toList());
            lines.remove(0);
            String[] maisJovem = lines.stream()
                    .map(l -> l.split(";"))
                    .sorted(Comparator.comparing(l -> l[2]))
                    .collect(Collectors.toList())
                    .get(0);

            System.out.println("Vencedor(a) mais jovem: " + maisJovem[3] + " - Idade:" + maisJovem[2] +
                    " - Ano do prêmio:" + maisJovem[1] + " - Nome do filme:" + maisJovem[4]);
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void maiorVencedor(String nomeArquivo) {
        try (Stream<String> stream = Files.lines(Paths.get(nomeArquivo))) {
            List<String> lines = stream.collect(Collectors.toList());
            lines.remove(0);

            Optional<String> maiorVencedor = lines.stream()
                    .map(l -> l.split(";"))
                    .map(l -> l[3])
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey);

            List<String[]> premiosMaiorVencedor = lines.stream()
                    .map(l -> l.split(";"))
                    .filter(l -> l[3].equals(maiorVencedor.get()))
                    .collect(Collectors.toList());

            System.out.println("Maior vencedor(a):" + maiorVencedor.get());
            premiosMaiorVencedor.forEach(p -> System.out.println("Ano:" + p[1] + " - Filme:" + p[4]));

        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void maiorVencedorVinteTrintaAnos(String filename) {
        try (Stream<String> stream = Files.lines(Paths.get(filename))) {
            List<String> lines = stream.collect(Collectors.toList());
            lines.remove(0);

            Optional<String> maiorVencedor = lines.stream()
                    .map(l -> l.split(";"))
                    .filter(l -> Integer.parseInt(l[2].replace(" ", "")) >= 20)
                    .filter(l -> Integer.parseInt(l[2].replace(" ", "")) <= 30)
                    .map(l -> l[3])
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()))
                    .entrySet()
                    .stream().max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey);

            List<String[]> premiosMaiorVencedorVinteTrintaAnos = lines.stream()
                    .map(l -> l.split(";"))
                    .filter(l -> Integer.parseInt(l[2].replace(" ", "")) >= 20)
                    .filter(l -> Integer.parseInt(l[2].replace(" ", "")) <= 30)
                    .filter(l -> l[3].equals(maiorVencedor.get()))
                    .collect(Collectors.toList());

            System.out.println("Maior vencedor(a):" + maiorVencedor.get());
            premiosMaiorVencedorVinteTrintaAnos.forEach(p -> System.out.println("Idade:" + p[2] + " anos - Ano:" + p[1]
                    + " - Filme:" + p[4]));

        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void maioresVencedores(String nomeArquivo1, String nomeArquivo2) {
        try (Stream<String> stream = Files.lines(Paths.get(nomeArquivo1))) {
            try (Stream<String> stream2 = Files.lines(Paths.get(nomeArquivo2))) {

                List<String[]> vencedores = concatLimpaStreams(stream, stream2);
                List<String[]> finalVencedores = vencedores;
                vencedores = vencedores.stream()
                        .filter(v ->
                                finalVencedores.stream()
                                        .filter(x -> v[3].equals(x[3]))
                                        .count() > 1
                        )
                        .sorted(Comparator.comparing(v -> v[3]))
                        .collect(Collectors.toList());


                vencedores.forEach(v -> System.out.println("Nome:" + v[3] + " - Ano:" + v[1] + " - Idade:" + v[2]
                        + " anos - Filme:" + v[4]));

            } catch (IOException e) {
                e.getStackTrace();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static void achaAtorPeloNome(String nomeArquivoMasculino, String nomeArquivoFeminino, String nomeAtor) {
        try (Stream<String> stream = Files.lines(Paths.get(nomeArquivoMasculino))) {
            try (Stream<String> stream2 = Files.lines(Paths.get(nomeArquivoFeminino))) {

                List<String[]> vencedores = concatLimpaStreams(stream, stream2);

                vencedores = vencedores.stream()
                        .filter(v -> v[3].replaceFirst(" ", "").equals(nomeAtor))
                        .sorted(Comparator.comparing(v -> v[3]))
                        .collect(Collectors.toList());

                System.out.println("Oscar(s) do(a) " + nomeAtor + ":");
                vencedores.forEach(v -> System.out.println("Ano:" + v[1] + " - Idade:" + v[2] + " anos - Filme:"
                        + v[4]));

            } catch (IOException e) {
                e.getStackTrace();
            }
        } catch (IOException e) {
            e.getStackTrace();
        }
    }

    private static List<String[]> concatLimpaStreams(Stream<String> stream, Stream<String> stream2) {
        Stream<String> concatStreams = Stream.concat(stream, stream2);
        List<String[]> vencedores = concatStreams
                .map(v -> v.split(";"))
                .sorted(Comparator.comparing(l -> l[0]))
                .collect(Collectors.toList());
        vencedores.remove(vencedores.size() - 1);
        vencedores.remove(vencedores.size() - 1);
        return vencedores;
    }
}
