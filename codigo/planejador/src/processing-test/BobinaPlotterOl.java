import processing.core.PApplet;
import java.util.List;

public class BobinaPlotter extends PApplet {
    private List<String[]> dadosBobina;

    public BobinaPlotter(List<String[]> dadosBobina) {
        this.dadosBobina = dadosBobina;
    }

    public void settings() {
        size(800, 600);
    }

    public void setup() {
        // Configurações iniciais
    }

    public void draw() {
        background(255);
        float raio = 200;
        float xCentro = width / 2;
        float yCentro = height / 2;
        float anguloInicial = -PI / 2;

        float anguloAtual = anguloInicial;
        float anguloAnterior = anguloInicial;
        float comprimentoTotal = 0;

        // Itera pelos dados da bobina
        for (String[] dados : dadosBobina) {
            float comprimento = Float.parseFloat(dados[0]);
            float largura = Float.parseFloat(dados[1]);

            // Calcula o ângulo de acordo com o comprimento
            float angulo = map(comprimento, 0, 100, 0, TWO_PI);

            // Calcula as coordenadas iniciais e finais do arco
            float xInicial = xCentro + raio * cos(anguloAnterior);
            float yInicial = yCentro + raio * sin(anguloAnterior);
            float xFinal = xCentro + raio * cos(anguloAnterior + angulo);
            float yFinal = yCentro + raio * sin(anguloAnterior + angulo);

            // Desenha o arco da bobina
            stroke(0);
            strokeWeight(2);
            noFill();
            arc(xCentro, yCentro, raio * 2, raio * 2, anguloAnterior, anguloAnterior + angulo);

            // Desenha uma linha reta conectando o final do arco atual com o início do próximo arco
            stroke(0, 255, 0);
            strokeWeight(1);
            line(xFinal, yFinal, xInicial, yInicial);

            // Atualiza os ângulos e o comprimento total
            anguloAnterior += angulo;
            comprimentoTotal += comprimento;
        }

        // Exibe o comprimento total da bobina
        textSize(20);
        textAlign(CENTER);
        fill(0);
        text("Comprimento Total: " + comprimentoTotal, width / 2, height - 20);
    }

    public static void main(String[] args) {
        List<String[]> dadosBobina = // Obtenha os dados extraídos do arquivo XLSX

        PApplet.main("BobinaPlotter", args);
    }
}
