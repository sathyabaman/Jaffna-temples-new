package baman.lankahomes.lk.jaffnatemples.mainClasses;


/**
 * Created by baman on 3/21/16.
 */
public class Domain {

    public String From_Lat_LNG;
    public String temple_type;

    public String get_main_domain(){
        String domain = "http://172.16.110.17/jaffnatempleAPI/";
        return domain;
    }


    public void Set_latng_City(String fromLocation){

        String lat, lng;

        switch (fromLocation) {
            case "Chankanai":
                lat = "9.748266"; lng = "79.970265";
                break;
            case "Jaffna Town":
                lat = "9.664740"; lng = "80.020788";
                break;
            case "Karainagar":
                lat = "9.745053"; lng = "79.881946";
                break;
            case "Karaveddy":
                lat = "9.800168"; lng = "80.200202";
                break;
            case "Kilinochchi":
                lat = "9.390484"; lng = "80.406473";
                break;
            case "Kopay":
                lat = "9.705922"; lng = "80.065380";
                break;
            case "Maruthankerney":
                lat = "9.622185"; lng = "80.396826";
                break;
            case "Nallur":
                lat = "9.673756"; lng = "80.033183";
                break;
            case "Point Pedro":
                lat = "9.824650"; lng = "80.236677";
                break;
            case "Sandilipay":
                lat = "9.742129"; lng = "79.986157";
                break;
            case "Skanthapuram":
                lat = "9.341890"; lng = "80.302674";
                break;
            case "Tellippalai":
                lat = "9.785668"; lng = "80.035347";
                break;
            case "Uduvil":
                lat = "9.732496"; lng = "80.008623";
                break;
            default:
                lat = "6.924832"; lng = "79.855990";
                break;
        }

        this.From_Lat_LNG = lat+","+lng;

    }


    public String getFrom_Lat_LNG(){
        return this.From_Lat_LNG;
    }

    public String setTempleType(String type){
        int value = Integer.parseInt(type);
        String tmp_type;
        switch (value){
            case 1:
                tmp_type = "Amman";
                break;
            case 2:
                tmp_type = "Anchaneyar";
                break;
            case 3:
                tmp_type = "Ayyappan";
                break;
            case 4:
                tmp_type = "Murugan";
                break;
            case 5 :
                tmp_type = "Pillaiyar";
                break;
            case 6:
                tmp_type = "Sivan";
                break;
            case 7:
                tmp_type = "Not Specified";
                break;
            default:
                tmp_type = "Not Specified";
                break;
        }
            this.temple_type = tmp_type;
        return temple_type;
    }

}
