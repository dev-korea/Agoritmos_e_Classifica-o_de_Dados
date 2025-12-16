import java.util.ArrayList;
import java.util.List;

public class paralelo extends circuito {

    private List<circuito> componentes= new ArrayList<>();

    public paralelo(List<circuito> componentes) {
        this.componentes.addAll(componentes);
    }

    public paralelo(){}

    public void add(circuito c){
        componentes.add(c);
    }

    public double getresistencia(){
        double inversototal=0;
        for(circuito c:componentes){
            inversototal+= 1.0/c.getresistencia();
        }
        return 1.0/inversototal;
    }
    @Override
    public String toString() {
        return "paralelo " + componentes.toString() + "=" + getresistencia();
    }
}
