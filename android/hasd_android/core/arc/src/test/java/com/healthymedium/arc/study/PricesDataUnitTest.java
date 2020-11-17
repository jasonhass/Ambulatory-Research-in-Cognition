//
// PricesDataUnitTest.java
//
// This file is part of the Ambulatory Research in Cognition (ARC) Project. It is subject
// to the license terms in the LICENSE file found in the top-level directory of this
// distribution and at 
// https://github.com/jasonhass/Ambulatory-Research-in-Cognition/blob/master/LICENSE
// No part of this Project, including this file, may be copied, modified, propagated, or
// distributed except according to the terms contained in the LICENSE file.

package com.healthymedium.arc.study;


import com.healthymedium.arc.utilities.PriceManager;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.junit.runners.Parameterized.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static junit.framework.TestCase.fail;


@RunWith(Parameterized.class)
public class PricesDataUnitTest {
    String DEFAULT_PATH = "src/main/res/";
    String FILE_NAME = "/price_sets.json";
    List<PriceManager.Item> currSession;

    @Parameter(0)
    public String filename;
    @Parameter(1)
    public int sessionId;
    @Parameter(2)
    public String[] items;
    @Parameter(3)
    public String[] prices;
    @Parameter(4)
    public String[] alts;

    @Before
    public void setup(){


        currSession = loadPrices(DEFAULT_PATH + filename + FILE_NAME).get( sessionId);
    }



    @Test
    public void test(){
        assertEquals(10, currSession.size());
        for(int x = 0; x < currSession.size(); x++){
            assertEquals(currSession.get(x).item, items[x]);
            assertEquals(currSession.get(x).price, prices[x]);
            assertEquals(currSession.get(x).alt, alts[x]);
        }
    }


    public List<List<PriceManager.Item>> loadPrices(String filename){

        File file = new File(filename);
        assertTrue(file.exists());
        InputStream stream = null;
        try {
            stream = new FileInputStream(file);
            assertNotNull(stream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            fail();
        }
       return PriceManager.loadJson(stream);
    }


    public void printSessionData(List<PriceManager.Item> session){
        System.out.println("{");
        System.out.println("    \""+filename+"\","+sessionId+",");

        String item = "    new String[]{";
        String price = "    new String[]{";
        String alt = "    new String[]{";
        for(int i =0; i < session.size(); i++){
            item += "\""+session.get(i).item+"\"";
            price += "\""+session.get(i).price+"\"";
            alt += "\""+session.get(i).alt+"\"";
            if(i != session.size() - 1){
                item+=",";
                price+=",";
                alt+=",";
            }
        }
        item+="},";
        price+="},";
        alt+="}";
        System.out.println(item);
        System.out.println(price);
        System.out.println(alt);
        System.out.println("},");
    }

    @Parameters (name = "{index}: {0} - {1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                {
                        "raw-de-rDE",335,
                        new String[]{"paprika","erdnüsse","batterien","bleistift","hemd","gummibärchen","büroklammern","wurst","haselnüsse","schnitzel"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-de-rDE",252,
                        new String[]{"spülmittel","büroklammern","erdnüsse","salat","zahnstocher","staubsaugerbeutel","waschmittel","nudeln","allzweckreiniger","klebeband"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },
                {
                        "raw-de-rDE",168,
                        new String[]{"fischstäbchen","servietten","batterien","taschentücher","mülltüten","toilettenpapier","wattestäbchen","öl","buch","spülschwamm"},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },
                {
                        "raw-en-rAU",335,
                        new String[]{"frozen peas","microwave dinners","window cleaner","shaving cream","napkins","museli bars","lamb chops","pasta sauce","paper towel","olive oil"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-en-rAU",252,
                        new String[]{"sponges","lamb chops","microwave dinners","coke","dishwashing liquid","light bulbs","peanut butter","toilet paper","mince beef","sunscreen"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },
                {
                        "raw-en-rAU",168,
                        new String[]{"conditioner","garlic bread","window cleaner","fish","tim tams","ice cream","muffins","green beans","Weet-bix","salad dressing"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },
                {
                        "raw-en-rCA",335,
                        new String[]{" bleach"," dryer sheets"," floss"," tin foil"," salad dressing"," celery"," trash bags"," applesauce"," pineapple"," conditioner"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-en-rCA",252,
                        new String[]{" dinner rolls"," trash bags"," dryer sheets"," almonds"," toilet paper"," cottage cheese"," detergent"," batteries"," light bulbs"," marshmallows"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },
                {
                        "raw-en-rCA",168,
                        new String[]{" cashews"," spinach"," floss"," paper towels"," limes"," aspirin"," pencils"," breakfast cereal"," napkins"," salad"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },
                {
                        "raw-en-rGB",335,
                        new String[]{"foil","cling film","air freshener","orange squash","plasters","batteries","cotton wool","cereal","hand cream","cucumber"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-en-rGB",252,
                        new String[]{"shower gel","cotton wool","cling film","pasta","bubble bath","sponges","pork","toilet roll","mince","window cleaner"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },
                {
                        "raw-en-rGB",168,
                        new String[]{"fish","light bulbs","air freshener","fabric conditioner","sweets","kitchen towel","conditioner","beef","yorkshire puddings","marmite"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },

                {
                        "raw-en-rIE",335,
                        new String[]{"foil","cling film","air freshener","orange squash","plasters","batteries","cotton wool","cereal","hand cream","cucumber"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-en-rIE",252,
                        new String[]{"shower gel","cotton wool","cling film","pasta","bubble bath","sponges","pork","toilet roll","mince","window cleaner"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },
                {
                        "raw-en-rIE",168,
                        new String[]{"fish","light bulbs","air freshener","fabric conditioner","sweets","kitchen towel","conditioner","beef","yorkshire puddings","marmite"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },
                {
                        "raw-en-rUS",335,
                        new String[]{"toilet paper","cucumber","salad","floss","zucchini","noodles","pineapple","aluminum foil","cashews","plastic wrap"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-en-rUS",252,
                        new String[]{"tortillas","pineapple","cucumber","almonds","aspirin","pencils","napkins","detergent","batteries","light bulbs"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },
                {
                        "raw-en-rUS",168,
                        new String[]{"bleach","cheeseburger","salad","lotion","blueberries","cereal","sponge","dish soap","celery","cooking spray"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },
                {
                        "raw-es-rAR",335,
                        new String[]{"detergente","aceite de oliva","ravioles","pilas","bolsa de carbón","cepillo de dientes","morrones","hamburguesas","pepino","cereales"},
                        new String[]{"73.90","83.40","79.20","76.30","91.60","26.50","91.20","34.70","69.30","25.30"},
                        new String[]{"16.90","58.70","63.50","92.50","51.70","79.10","78.40","51.90","14.80","78.60"}
                },
                {
                        "raw-es-rAR",252,
                        new String[]{"trapo de piso","morrones","aceite de oliva","fideos","maquinita de afeitar","cuaderno","queso crema","crema de enjuague","chupetín","lamparita"},
                        new String[]{"15.40","41.20","95.10","19.70","73.20","13.50","45.70","35.40","25.30","15.70"},
                        new String[]{"57.30","24.60","37.90","53.20","53.70","59.20","67.40","83.40","78.10","31.90"}
                },
                {
                        "raw-es-rAR",168,
                        new String[]{"asado","espinaca","ravioles","pañales","esponja","yerba","repelente","algodón","lata de arvejas","edulcorante"},
                        new String[]{"41.90","35.20","28.70","45.90","84.30","62.70","67.90","81.60","21.30","64.30"},
                        new String[]{"74.50","51.30","63.70","72.40","61.30","21.90","17.30","57.40","94.80","91.50"}
                },

                {
                        "raw-es-rCO",335,
                        new String[]{"arequipe","cepillo de dientes","periódico","revista","tenedor","papel","tijeras","papa criolla","libro","yuca"},
                        new String[]{"73.900","83.400","79.200","76.300","91.600","26.500","91.200","34.700","69.300","25.300"},
                        new String[]{"16.900","58.700","63.500","92.500","51.700","79.100","78.400","51.900","14.800","78.600"}
                },
                {
                        "raw-es-rCO",252,
                        new String[]{"servilletas","tijeras","cepillo de dientes","papel de baño","escoba","medias","chicharrón","arepa","buñuelo","taza"},
                        new String[]{"15.400","41.200","95.100","19.700","73.200","13.500","45.700","35.400","25.300","15.700"},
                        new String[]{"57.300","24.600","37.900","53.200","53.700","59.200","67.400","83.400","78.100","31.900"}
                },

                {
                        "raw-es-rCO",168,
                        new String[]{"empanadas","guantes","periódico","cuchillo","esmalte de uñas","espagueti","espinaca","detergente para la ropa","lápices","plato"},
                        new String[]{"41.900","35.200","28.700","45.900","84.300","62.700","67.900","81.600","21.300","64.300"},
                        new String[]{"74.500","51.300","63.700","72.400","61.300","21.900","17.300","57.400","94.800","91.500"}
                },
                {
                        "raw-es-rES",335,
                        new String[]{"garbanzos","boligrafos","estropajo","avellanas","tinte","lejia","guantes","cepillo de dientes","cacahuetes","chicles"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-es-rES",252,
                        new String[]{"mermelada","guantes","boligrafos","pasta","borrador","piruleta","gambas","detergente","esponja","apio"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },
                {
                        "raw-es-rES",168,
                        new String[]{"cremacorporal","cerillas","estropajo","limpiacristales","manzanilla","judias verdes","puerro","cerezas","berenjenas","escoba"},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },

                {
                        "raw-es-rMX",335,
                        new String[]{"papel de baño","paletas de hielo","cacahuates","crema de afeitar","comida de bebe","cepillo de dientes","escoba","lápiz","gel para el cabello","aceite"},
                        new String[]{"73.90","83.40","79.20","76.30","91.60","26.50","91.20","34.70","69.30","25.30"},
                        new String[]{"16.90","58.70","63.50","92.50","51.70","79.10","78.40","51.90","14.80","78.60"}
                },
                {
                        "raw-es-rMX",252,
                        new String[]{"lápiz labial","escoba","paletas de hielo","cuchillo","pluma","platos de papel","suavizante de ropa","tortillas","goma de borrar","limpiador de vidrios"},
                        new String[]{"15.40","41.20","95.10","19.70","73.20","13.50","45.70","35.40","25.30","15.70"},
                        new String[]{"57.30","24.60","37.90","53.20","53.70","59.20","67.40","83.40","78.10","31.90"}
                },
                {
                        "raw-es-rMX",168,
                        new String[]{"servilletas","vasos desechables","cacahuates","elotes","hielo","periódico","gel antibacterial de manos","garbanzos","revistas","papel aluminio"},
                        new String[]{"41.90","35.20","28.70","45.90","84.30","62.70","67.90","81.60","21.30","64.30"},
                        new String[]{"74.50","51.30","63.70","72.40","61.30","21.90","17.30","57.40","94.80","91.50"}
                },
                {
                        "raw-es-rUS",335,
                        new String[]{"bolsas de basura","fideos","flores","piña","vasos desechables","cuchillo","sándwich","apio","hielo","detergente"},
                        new String[]{"7.39","8.34","7.92","7.63","9.16","2.65","9.12","3.47","6.93","2.53"},
                        new String[]{"1.69","5.87","6.35","9.25","5.17","7.91","7.84","5.19","1.48","7.86"}
                },
                {
                        "raw-es-rUS",252,
                        new String[]{"espinacas","sándwich","fideos","aceite vegetal","sandia","tortillas","espátula","blanqueador","fresas","tijeras"},
                        new String[]{"1.54","4.12","9.51","1.97","7.32","1.35","4.57","3.54","2.53","1.57"},
                        new String[]{"5.73","2.46","3.79","5.32","5.37","5.92","6.74","8.34","7.81","3.19"}
                },

                {
                        "raw-es-rUS",168,
                        new String[]{"cloro ","papel de baño","flores","galletas","frijoles","baterías ","helado","cilantro","fruta seca","hilo dental"},
                        new String[]{"4.19","3.52","2.87","4.59","8.43","6.27","6.79","8.16","2.13","6.43"},
                        new String[]{"7.45","5.13","6.37","7.24","6.13","2.19","1.73","5.74","9.48","9.15"}
                },

                {
                        "raw-fr-rCA",335,
                        new String[]{"papier hygienique","stylo","ballons","frisbee","pinces","celeri","gourde","framboise","médicaments","vitamines"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-fr-rCA",252,
                        new String[]{"fleurs","gourde","stylo","kiwis","chapeau","chandelles","épices","tangerines","mousse","vernis à ongles"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },

                {
                        "raw-fr-rCA",168,
                        new String[]{"céréales","pot","ballons","papier","livre","muffin","glace","caramel","noix","concombre"},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },
                {
                        "raw-fr-rFR",335,
                        new String[]{"brosse à dents","glace","haricots verts","papier aluminium","betterave","salade","allumettes","chips","éponges","biscottes"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-fr-rFR",252,
                        new String[]{"coton-tiges","allumettes","glace","céréales","journal","épinards","raviolis","papier toilette","poisson pané","enveloppe"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },
                {
                        "raw-fr-rFR",168,
                        new String[]{"huile d'olive","aubergine","haricots verts","crayons","mouchoirs","brioche","crème hydratante","concombre","basilic","javel"},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },
                {
                        "raw-it-rIT",335,
                        new String[]{"fette biscottate","pellicola per alimenti","schiuma da barba","mutande","crema per il corpo","sedano","rivista","carta igienica","libro","rasoio"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-it-rIT",252,
                        new String[]{"gomma da masticare","rivista","pellicola per alimenti","pasta","dado da brodo","candeggina","espresso","fazzoletti di carta","lievito","quaderni"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },
                {
                        "raw-it-rIT",168,
                        new String[]{"patate fritte","carta da forno","schiuma da barba","penna","spugna","insalata","grissini","panino","sacchetto","cotton fioc"},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },
                {
                        "raw-ja-rJP",335,
                        new String[]{"キャベツ","ジュース","サーモン","せっけん","みかん","ねぎ","焼肉","パン","はし","キッチンスポンジ"},
                        new String[]{"739.00","834.00","792.00","763.00","916.00","265.00","912.00","347.00","693.00","253.00"},
                        new String[]{"169.00","587.00","635.00","925.00","517.00","791.00","784.00","519.00","148.00","786.00"}
                },
                {
                        "raw-ja-rJP",252,
                        new String[]{"バター","焼肉","ジュース","コーヒー","ぎゅにゅ","ベーコン","チーズ","水","ビール","パスタ"},
                        new String[]{"154.00","412.00","951.00","197.00","732.00","135.00","457.00","354.00","253.00","157.00"},
                        new String[]{"573.00","246.00","379.00","532.00","537.00","592.00","674.00","834.00","781.00","319.00"}
                },
                {
                        "raw-ja-rJP",168,
                        new String[]{"魚","みそ汁","サーモン","キットカット","ヨーグルト","肉","おにぎり","チョコレート","牛乳","野菜"},
                        new String[]{"419.00","352.00","287.00","459.00","843.00","627.00","679.00","816.00","213.00","643.00"},
                        new String[]{"745.00","513.00","637.00","724.00","613.00","219.00","173.00","574.00","948.00","915.00"}
                },
                {
                        "raw-nl-rNL",335,
                        new String[]{"komkommer","scheermesjes","allesreiniger ","vaatdoek","tijdschrift ","ontbijtgranen","pleisters","toiletpapier","batterijen ","beschuit"},
                        new String[]{"7,39","8,34","7,92","7,63","9,16","2,65","9,12","3,47","6,93","2,53"},
                        new String[]{"1,69","5,87","6,35","9,25","5,17","7,91","7,84","5,19","1,48","7,86"}
                },
                {
                        "raw-nl-rNL",252,
                        new String[]{"hagelslag","pleisters","scheermesjes","pasta","knakworsten","servetten","ontbijtkoek","aardbeien","citroen","kattenvoer"},
                        new String[]{"1,54","4,12","9,51","1,97","7,32","1,35","4,57","3,54","2,53","1,57"},
                        new String[]{"5,73","2,46","3,79","5,32","5,37","5,92","6,74","8,34","7,81","3,19"}
                },
                {
                        "raw-nl-rNL",168,
                        new String[]{"cola","kiwi","allesreiniger ","aluminiumfolie","bakmeel","roomijs","drop","olijfolie","handdoek","nagellak "},
                        new String[]{"4,19","3,52","2,87","4,59","8,43","6,27","6,79","8,16","2,13","6,43"},
                        new String[]{"7,45","5,13","6,37","7,24","6,13","2,19","1,73","5,74","9,48","9,15"}
                },

        });
    }

}
