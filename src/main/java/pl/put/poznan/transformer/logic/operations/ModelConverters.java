package pl.put.poznan.transformer.logic.operations;

import com.jayway.jsonpath.JsonPath;
import net.minidev.json.JSONArray;
import pl.put.poznan.transformer.logic.models.scenarios.Scenario;
import pl.put.poznan.transformer.logic.models.scenarios.items.StepBasicOperation;
import pl.put.poznan.transformer.logic.models.scenarios.items.StepForEach;
import pl.put.poznan.transformer.logic.models.scenarios.items.StepIfElse;
import pl.put.poznan.transformer.rest.models.RawScenario;

import java.util.ArrayList;
import java.util.LinkedHashMap;


public class ModelConverters {

    private static Scenario findScenario(JSONArray steps){
        Scenario scenario = new Scenario();
        for (Object step : steps) {
        String keyword = ((LinkedHashMap) step).get("Keyword").toString();
        String name = ((LinkedHashMap) step).get("Name").toString();
        JSONArray stepsDeep = (JSONArray)((LinkedHashMap) step).get("Steps");
            if(keyword.equals("IF") || keyword.equals("ELSE")){
                scenario.add(new StepIfElse(name, findScenario(stepsDeep)));
            }
            else if (keyword.equals("FOREACH")){
                scenario.add(new StepForEach(name, findScenario(stepsDeep)));
            }
            else{   //bez keywordów
                scenario.add(new StepBasicOperation(name));
            }
        }
        return scenario;
    }

    public static Scenario RawToScenario(RawScenario rawScenario){
        String json = rawScenario.getScenarioJson();
        String title = JsonPath.read(json, "$.Title");
        String system_actor = JsonPath.read(json, "$.['System Actor']");
        ArrayList<String> actors = JsonPath.read(json, "$.Actors[*]");
        JSONArray steps = JsonPath.read(json, "$.Steps[*]");
        return findScenario(steps);
    }
}