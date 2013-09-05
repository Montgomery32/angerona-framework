package angerona.fw.def;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import angerona.fw.Action;
import angerona.fw.Agent;
import angerona.fw.Angerona;
import angerona.fw.AngeronaEnvironment;
import angerona.fw.EnvironmentBehavior;
import angerona.fw.Perception;

/**
 * Behavior implementing the default Angerona environment behavior.
 * runOneTick will wait till isSimulationReady returns true.
 * @author Tim Janus
 */
public class DefaultBehavior implements EnvironmentBehavior  {

	private static Logger LOG = LoggerFactory.getLogger(DefaultBehavior.class);
	
	protected boolean doingTick = false;
	
	protected boolean angeronaReady = true;
	
	/** the actual simulation tick */
	protected int tick = 0;
	
	
	@Override
	public void sendAction(AngeronaEnvironment env, Action act) {
		// The action send by one agent is the perception of the other one.
		String agentName = act.getReceiverId();
		localDelegate(env, act, agentName);
	}

	@Override
	public void receivePerception(AngeronaEnvironment env, Perception percept) {
		String agentName = percept.getReceiverId();
		localDelegate(env, percept, agentName);
	}
	
	/**
	 * Helper method: delegates the perception/action to the local agents.
	 * @param env
	 * @param percept
	 * @param agentName
	 */
	protected void localDelegate(AngeronaEnvironment env, Perception percept, String agentName) {
		if(Action.ALL.equals(agentName)) {
			for(Agent agent : env.getAgents()) {
				agent.perceive(percept);
			}
		} else {
			Agent ag = env.getAgentByName(agentName);
			if(ag != null) {
				ag.perceive(percept);
			} else {
				LOG.warn("Action/Perception was not send, agent '{}' was not found in environment.", agentName);
			}
		}
	}

	@Override
	public boolean runOneTick(AngeronaEnvironment env) {
		doingTick = true;
		
		while(!isSimulationReady()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		boolean somethingHappens = false;
		for(Agent agent : env.getAgents()) {
			if(agent.hasPerceptions()) {
				somethingHappens = true;
			}
		}
		
		if(!somethingHappens && tick != 0)
			return false;
		
		angeronaReady = false;
		++tick;
		for(Agent agent : env.getAgents()) {
			// cycle internally sends the selected action
			// to the environment using sendAction() method.
			agent.cycle();
		}
		angeronaReady = true;
		
		doingTick = false;
		Angerona.getInstance().onTickDone(env);
		return true;
	}

	@Override
	public boolean run(AngeronaEnvironment env) {
		boolean reval = false;
		while(reval = runOneTick(env));
		return reval;
	}

	@Override
	public boolean isAngeronaReady() {
		return angeronaReady;
	}

	@Override
	public boolean isSimulationReady() {
		return true;
	}

	@Override
	public boolean isDoingTick() {
		return doingTick;
	}

	@Override
	public int getTick() {
		return tick;
	}

}
