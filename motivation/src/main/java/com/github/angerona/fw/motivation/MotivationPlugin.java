package com.github.angerona.fw.motivation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.xeoh.plugins.base.annotations.PluginImplementation;

import com.github.angerona.fw.AgentComponent;
import com.github.angerona.fw.AngeronaPluginAdapter;
import com.github.angerona.fw.EnvironmentBehavior;
import com.github.angerona.fw.gui.UIPlugin;
import com.github.angerona.fw.gui.base.ViewComponent;
import com.github.angerona.fw.logic.BaseTranslator;
import com.github.angerona.fw.motivation.dao.impl.DefaultRanges;
import com.github.angerona.fw.motivation.dao.impl.LevelWeights;
import com.github.angerona.fw.motivation.dao.impl.MotStructure;
import com.github.angerona.fw.motivation.dao.impl.MotiveCouplings;
import com.github.angerona.fw.motivation.dao.impl.WeightRanges;
import com.github.angerona.fw.motivation.island.IslandBehavior;
import com.github.angerona.fw.motivation.island.IslandTranslator;
import com.github.angerona.fw.motivation.island.comp.Area;
import com.github.angerona.fw.motivation.island.comp.Battery;
import com.github.angerona.fw.motivation.island.comp.IslandCouplings;
import com.github.angerona.fw.motivation.island.comp.IslandDesires;
import com.github.angerona.fw.motivation.island.operators.BdiSelectionOperator;
import com.github.angerona.fw.motivation.island.operators.IslandActionOperator;
import com.github.angerona.fw.motivation.island.view.AreaView;
import com.github.angerona.fw.motivation.island.view.BatteryView;
import com.github.angerona.fw.motivation.island.view.IslandCouplingsView;
import com.github.angerona.fw.motivation.island.view.IslandDesiresView;
import com.github.angerona.fw.motivation.view.CouplingsView;
import com.github.angerona.fw.motivation.view.DefRangesView;
import com.github.angerona.fw.motivation.view.GenCouplingsView;
import com.github.angerona.fw.motivation.view.GenRangesView;
import com.github.angerona.fw.motivation.view.GenWeightsView;
import com.github.angerona.fw.motivation.view.MotStructureView;
import com.github.angerona.fw.motivation.view.RangesView;
import com.github.angerona.fw.motivation.view.WeightsView;
import com.github.angerona.fw.operators.BaseOperator;

/**
 * 
 * @author Manuel Barbi
 * 
 */
@PluginImplementation
public class MotivationPlugin extends AngeronaPluginAdapter implements UIPlugin {

	@Override
	public List<Class<? extends AgentComponent>> getAgentComponentImpl() {
		List<Class<? extends AgentComponent>> reval = new ArrayList<>();
		reval.add(MotiveCouplings.class);
		reval.add(WeightRanges.class);
		reval.add(LevelWeights.class);
		reval.add(MotStructure.class);
		reval.add(DefaultRanges.class);
		reval.add(IslandDesires.class);
		reval.add(IslandCouplings.class);
		reval.add(Battery.class);
		reval.add(Area.class);
		return reval;
	}

	@Override
	public Map<String, Class<? extends ViewComponent>> getUIComponents() {
		Map<String, Class<? extends ViewComponent>> reval = new HashMap<String, Class<? extends ViewComponent>>();
		reval.put("MotiveCoupling-View", CouplingsView.class);
		reval.put("MotiveCoupling-View", IslandCouplingsView.class);
		reval.put("WeightRange-View", RangesView.class);
		reval.put("WeightRange-View", DefRangesView.class);
		reval.put("LevelWeight-View", WeightsView.class);
		reval.put("MotStructure-View", MotStructureView.class);
		reval.put("Area-View", AreaView.class);
		reval.put("Battery-View", BatteryView.class);
		reval.put("IslandDesires-View", IslandDesiresView.class);
		return reval;
	}

	@Override
	public List<Class<? extends BaseOperator>> getOperators() {
		List<Class<? extends BaseOperator>> reval = new ArrayList<>();
		reval.add(MotivationOperator.class);
		reval.add(MotivationFilterOperator.class);
		reval.add(BdiSelectionOperator.class);
		reval.add(IslandActionOperator.class);
		return reval;
	}

	@Override
	public List<Class<? extends BaseTranslator>> getTranslatorImpl() {
		List<Class<? extends BaseTranslator>> reval = new ArrayList<>();
		reval.add(IslandTranslator.class);
		return reval;
	}

	@Override
	public List<Class<? extends EnvironmentBehavior>> getEnvironmentBehaviors() {
		List<Class<? extends EnvironmentBehavior>> reval = new ArrayList<>();
		reval.add(IslandBehavior.class);
		return reval;
	}

}