/***********************************************************************************************************************
 *
 * Copyright (C) 2010-2013 by the Stratosphere project (http://stratosphere.eu)
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 *
 **********************************************************************************************************************/
package eu.stratosphere.api.java.operators.translation;

import eu.stratosphere.api.common.functions.GenericGroupReduce;
import eu.stratosphere.api.common.operators.base.GroupReduceOperatorBase;
import eu.stratosphere.api.java.functions.GroupReduceFunction;
import eu.stratosphere.api.java.operators.Keys;
import eu.stratosphere.api.java.tuple.Tuple2;
import eu.stratosphere.api.java.typeutils.TypeInformation;
import eu.stratosphere.util.Collector;

import java.util.Iterator;

/**
 *
 */
public class PlanUnwrappingReduceGroupOperator<IN, OUT, K> extends GroupReduceOperatorBase<GenericGroupReduce<Tuple2<K, IN>,OUT>>
	implements UnaryJavaPlanNode<Tuple2<K, IN>, OUT>
{
	private final TypeInformation<OUT> outputType;
	
	private final TypeInformation<Tuple2<K, IN>> typeInfoWithKey;


	public PlanUnwrappingReduceGroupOperator(GroupReduceFunction<IN, OUT> udf, Keys.SelectorFunctionKeys<IN, K> key, String name,
			TypeInformation<IN> inType, TypeInformation<OUT> outType, TypeInformation<Tuple2<K, IN>> typeInfoWithKey)
	{
		super(new TupleUnwrappingGroupReducer<IN, OUT, K>(udf), key.computeLogicalKeyPositions(), name);
		
		this.outputType = outType;
		this.typeInfoWithKey = typeInfoWithKey;
	}
	
	
	@Override
	public TypeInformation<OUT> getReturnType() {
		return this.outputType;
	}

	@Override
	public TypeInformation<Tuple2<K, IN>> getInputType() {
		return this.typeInfoWithKey;
	}
	
	
	// --------------------------------------------------------------------------------------------
	
	public static final class TupleUnwrappingGroupReducer<IN, OUT, K> extends WrappingFunction<GroupReduceFunction<IN, OUT>>
		implements GenericGroupReduce<Tuple2<K, IN>, OUT>
	{

		private static final long serialVersionUID = 1L;
		
		private TupleUnwrappingIterator<IN, K> iter;
		
		private TupleUnwrappingGroupReducer(GroupReduceFunction<IN, OUT> wrapped) {
			super(wrapped);
		}


		@Override
		public void reduce(Iterator<Tuple2<K, IN>> values, Collector<OUT> out) throws Exception {
			iter.set(values);

			this.wrappedFunction.reduce(iter, out);
		}

		@Override
		public void combine(Iterator<Tuple2<K, IN>> values, Collector<Tuple2<K, IN>> out) throws Exception {
//			iter.set(values);
//
//			@SuppressWarnings("unchecked")
//			ReferenceWrappingCollector<IN> combColl = (ReferenceWrappingCollector<IN>) coll;
//
//			combColl.set(out);
//
//			this.wrappedFunction.reduce(iter, coll);
		}
	}
}
