package ai.beans;
public class ColorBean {
private String foregroundColor;
private String backgroundColor;
private String border;
public ColorBean() {
    
}

    public String getForegroundColor() {
        return foregroundColor;
    }

    public void setForegroundColor(String foregroundColor) {
        this.foregroundColor = foregroundColor;
        System.out.println(foregroundColor);
    }
    
    public void setBorder(String border) {
      this.border = border;
    }
    public String getBackgroundColor() {
        System.out.println(foregroundColor);
        System.out.println(1);
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

}