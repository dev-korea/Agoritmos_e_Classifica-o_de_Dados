public class resistor extends circuito {
    private double resistencia;

    public resistor(double resistencia) {
        this.resistencia = resistencia;
    }

    public double getresistencia() {
        return resistencia;
    }

    public String toString() {
        return "resistencia: " + resistencia;
    }
}
