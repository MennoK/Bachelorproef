package sequences;
import helpers.DataSet;
import weka.classifiers.Classifier;
import weka.core.Instances;

public class SequenceEvaluator {

	Classifier classifier;
	
	public SequenceEvaluator(String pathToModel) throws Exception {
		this.classifier = (Classifier) weka.core.SerializationHelper.read(pathToModel);
	}
	
	public String[] getPredictedLabels(String pathToTestSet) throws Exception {
		DataSet testData = new DataSet(pathToTestSet);
		Instances test = testData.instances;
		String[] labels = new String[test.numInstances()];
		for (int i = 0; i < test.numInstances(); i++) {
			  double pred = classifier.classifyInstance(test.instance(i));
			  labels[i] = test.classAttribute().value((int) pred);
		}
		return labels;
	}
	
}
