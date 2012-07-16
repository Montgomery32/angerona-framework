package angerona.fw.operators.def;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.Formula;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Literal;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Program;
import net.sf.tweety.logicprogramming.asplibrary.syntax.Rule;
import net.sf.tweety.logicprogramming.asplibrary.util.AnswerSet;
import net.sf.tweety.logics.firstorderlogic.syntax.FolFormula;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import angerona.fw.BaseBeliefbase;
import angerona.fw.comm.Answer;
import angerona.fw.comm.DetailQueryAnswer;
import angerona.fw.logic.ConfidentialKnowledge;
import angerona.fw.logic.Secret;
import angerona.fw.logic.SecrecyStrengthPair;
import angerona.fw.logic.asp.AspBeliefbase;
import angerona.fw.logic.asp.AspReasoner;
import angerona.fw.operators.parameter.ViolatesParameter;
/**
 * Extension of my DetailSimpleViolatesOperator which enables one to weaken secrecy
 * @author dilger
 *
 */
public class WeakeningViolatesOperator extends DetailSimpleViolatesOperator {
	static final double INFINITY = 1000.0;
	
	@Override
	protected Boolean processInt(ViolatesParameter param) {
		this.weakenings = processIntAndWeaken(param);
		return super.processInt(param);
	}
	private Rule convertToRule(Formula f)
	{
		return null;
	}
	private List<SecrecyStrengthPair> representTotalExposure(ConfidentialKnowledge conf)
	{
		return null;
	}
	private boolean formulaMatchesLiteral(Formula secretInfo, Literal literal)
	{
		return false;
	}
	private double calculateSecrecyStrength(Formula secretInfo, List<AnswerSet> ansSets)
	{
		return 0.0;
	}
	protected List<SecrecyStrengthPair> processIntAndWeaken(ViolatesParameter param)
	{
		Logger LOG = LoggerFactory.getLogger(DetailViolatesOperator.class);
		List<SecrecyStrengthPair> secretList = new LinkedList<SecrecyStrengthPair>(); 
		
		/* Check if any confidential knowledge present. If none then no secrecy weakening possible */
		ConfidentialKnowledge conf = param.getAgent().getComponent(ConfidentialKnowledge.class);
		if(conf == null)
			return secretList;
		
		/* Remaining operations depend on whether the action in question is an answer */
		if(param.getAction() instanceof Answer) 
		{
			Answer a = (Answer) param.getAction();
			Map<String, BaseBeliefbase> views = param.getBeliefs().getViewKnowledge();
			if(views.containsKey(a.getReceiverId())) 
			{
				
				AspBeliefbase view = (AspBeliefbase) views.get(a.getReceiverId()).clone();
				Program prog = view.getProgram();
				
				DetailQueryAnswer dqa = ((DetailQueryAnswer) a);
				LOG.info("Make Revision for DetailQueryAnswer: '{}'", dqa.getDetailAnswer());
				
				FolFormula answerFormula = dqa.getDetailAnswer();
				Rule rule = convertToRule(answerFormula);
				
				prog.add(rule);
				
				/*Check for contradictions. If one is found consider all secrets totally revealed*/ 
				List<AnswerSet> newAnsSets = null; //Should this be a set? Will it pass as a list?
				//This try/catch may be a crude solution but...
				//Actually, is try/catch even necessary in this case?
				try
				{
					newAnsSets = ((AspReasoner) view.getReasoningOperator()).processAnswerSets();
				}
				catch (IndexOutOfBoundsException ie)
				{
					
				}
				if (newAnsSets==null)
				{
					report(param.getAgent().getName() + "' creates contradiction by: '" + param.getAction() + "'", view);
					secretList = representTotalExposure(conf);
					return secretList;
				}
				
				/* Now the secrecy strengths get added */
				for(Secret secret : conf.getTargets()) 
				{
					Formula secretInfo = secret.getInformation(); //This differs slightly from my pseudocode...
					Rule secretRule = convertToRule(secretInfo);
					if(prog.contains(secretRule))
					{
						report(param.getAgent().getName() + "' creates contradiction by: '" + param.getAction() + "'", view);
						SecrecyStrengthPair sPair = new SecrecyStrengthPair();
						sPair.defineSecret(secret);
						double strength = calculateSecrecyStrength(secretInfo, newAnsSets);
						//Not sure how to access the operator yet
					}
					
				}
			}
		}
		
		return secretList;
	}
}
