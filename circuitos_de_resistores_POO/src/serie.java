import java.util.ArrayList;
import java.util.List;

public class serie extends circuito {

    private List<circuito> componentes= new ArrayList<>();

    public serie(List<circuito> componentes) {
        this.componentes.addAll(componentes);
    }

    public serie(){}

    public void add(circuito c){
        componentes.add(c);
    }

    public double getresistencia(){
        double total=0;
        for(circuito c:componentes){
            total+=c.getresistencia();
        }
        return total;
    }
    @Override
    public String toString() {
        return "serie " + componentes.toString() + "=" + getresistencia();
    }
}
