package nicodo.com.myemail.models;

public class Improvement {

    private String name;
    private int price;
    private boolean isSelected = false;

    public Improvement(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public void setSelected(boolean selected){
        isSelected = selected;
    }

    public boolean isSelected(){
        return isSelected;
    }
}
