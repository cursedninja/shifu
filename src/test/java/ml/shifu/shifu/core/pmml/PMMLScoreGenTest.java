package ml.shifu.shifu.core.pmml;

import ml.shifu.shifu.core.pmml.builder.creator.AbstractSpecifCreator;
import org.dmg.pmml.FieldName;
import org.dmg.pmml.PMML;
import org.jpmml.evaluator.ClassificationMap;
import org.jpmml.evaluator.FieldValue;
import org.jpmml.evaluator.NeuralNetworkEvaluator;

import java.util.List;
import java.util.Map;

/**
 * Created by zhanhu on 2/8/17.
 */
public class PMMLScoreGenTest {

    //@Test
    public void verifyPmml() throws Exception {
        String dataPath = "src/test/resources/example/pmml-test/test-data.line100";
        String delimiter = ",";

        PMML pmml = PMMLUtils.loadPMML("src/test/resources/example/pmml-test/ATOM17_SEG3_35.pmml");
        NeuralNetworkEvaluator evaluator = new NeuralNetworkEvaluator(pmml);

        List<Map<FieldName, FieldValue>> input = CsvUtil.load(evaluator, dataPath, delimiter);
        for (Map<FieldName, FieldValue> rawLine : input) {
            int score = runPmmlModel(evaluator, rawLine);
            System.out.println(score);
        }
    }

    @SuppressWarnings("unchecked")
    private int runPmmlModel(NeuralNetworkEvaluator evaluator, Map<FieldName, FieldValue> rawInput) {
        switch (evaluator.getModel().getFunctionName()) {
            case REGRESSION:
                Map<FieldName, Double> regressionTerm = (Map<FieldName, Double>) evaluator.evaluate(rawInput);
                return regressionTerm.get(new FieldName(AbstractSpecifCreator.FINAL_RESULT)).intValue();
            case CLASSIFICATION:
                Map<FieldName, ClassificationMap<String>> classificationTerm =
                        (Map<FieldName, ClassificationMap<String>>) evaluator.evaluate(rawInput);
                for (ClassificationMap<String> cMap : classificationTerm.values()) {
                    for (Map.Entry<String, Double> entry : cMap.entrySet()) {
                        return (int) (entry.getValue() * 1000);
                    }
                }
            default:
                return -1;
        }
    }

}
