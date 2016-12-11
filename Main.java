import egonetwork.SocialCircleInduction;
import ingester.Kaggle;

public class Main {

	public static void main(String[] args){
		
		SocialCircleInduction.McAuleyInduction(Kaggle.createTestableEgoNetwork());
		
	}
	
}
