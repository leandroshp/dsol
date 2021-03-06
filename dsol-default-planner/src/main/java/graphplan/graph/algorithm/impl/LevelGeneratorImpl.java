/*
 * ---------------------------------------------------------------------------
 * Copyright (C) 2010  Felipe Meneguzzi
 * JavaGP is distributed under LGPL. See file LGPL.txt in this directory.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 * 
 * To contact the author:
 * http://www.meneguzzi.eu/felipe/contact.html
 * ---------------------------------------------------------------------------
 */
package graphplan.graph.algorithm.impl;

import graphplan.domain.Operator;
import graphplan.domain.Proposition;
import graphplan.flyweight.OperatorFactory;
import graphplan.flyweight.OperatorFactoryException;
import graphplan.graph.ActionLevel;
import graphplan.graph.PlanningGraphException;
import graphplan.graph.PropositionLevel;
import graphplan.graph.algorithm.ActionLevelGenerator;
import graphplan.graph.algorithm.PropositionLevelGenerator;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

public class LevelGeneratorImpl implements ActionLevelGenerator, PropositionLevelGenerator {
	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ActionLevelGenerator.class.getName());

	/*
	 * TODO Optimize this method
	 * (non-Javadoc)
	 * @see graphplan.graph.algorithm.ActionLevelGenerator#createNextActionLevel(graphplan.graph.PropositionLevel)
	 */
	public ActionLevel createNextActionLevel(PropositionLevel propositionLevel) throws PlanningGraphException {
		final ActionLevel actionLevel = new ActionLevel();
		
		final OperatorFactory opFactory = OperatorFactory.getInstance();
		final LinkedHashSet<Operator> opTemplateSet = new LinkedHashSet<Operator>();
		final Set<Operator> opSet = new LinkedHashSet<Operator>();
		final Set<Proposition> preconds = new HashSet<Proposition>();
		
		
		//TODO Change this to scan by operator rather than by proposition
		//operatorTemplate
		final List<Operator> templates = opFactory.getRequiringOperatorTemplates(propositionLevel);
		opTemplateSet.addAll(templates);

//		// For every proposition
		for (Proposition proposition : propositionLevel) {
			//final List<Operator> templates;
			//Gather potential operator templates
			//templates = opFactory.getRequiringOperatorTemplates(proposition);
			//opTemplateSet.addAll(templates);
			//Add all noops
			opSet.add(opFactory.getNoop(proposition));
			//And prepare the list of preconditons for later
			preconds.add(proposition);
		}		
		opTemplateSet.addAll(opFactory.getRequiringOperatorTemplates((Proposition)null));


		/*for (Proposition proposition : propositionLevel) {
			preconds.add(proposition);
		}*/
		
		
		//Piece of crap algorithm used before has been replaced by this call
		try {
			Set<Operator> operators = opFactory.getAllPossibleInstantiations(new ArrayList<Operator>(opTemplateSet), preconds);
			opSet.addAll(operators);
		} catch (OperatorFactoryException e) {
			throw new PlanningGraphException(e.getMessage(),propositionLevel.getIndex()+1);
		}
		for (Operator operator : opSet) {
			actionLevel.addAction(operator);
		}
		


		// TODO discover how to properly instantiate operator templates
		// TODO optimize this algorithm
		
		return actionLevel;
	}
	
	public PropositionLevel createNextPropositionLevel(ActionLevel actionLevel) throws PlanningGraphException {
		PropositionLevel propositionLevel = new PropositionLevel();
		for (Operator operator : actionLevel) {
			propositionLevel.addPropositions(operator.getEffects());
		}
		return propositionLevel;
	}

}
