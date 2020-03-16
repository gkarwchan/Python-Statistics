optimizer:
----------
For each Site constraint (usually one):
    call Site_Maskwa_steam_recommendation:
	params:
	    site_data: for each trunk -> pad : {
            data (csv file / dropped unnamed column)
            max_constraints (well constraints)
            model
            pad_constraints: (pad constraints)
	    }
	    scenario_id
	    step_size: 100
	    steam_site_daily_inject: 1100
	    steam_site_constraint_daily : 1100
	    trunk_constraints: (trunk constraints)
	    well_statuses: (well statuses)
	    start_date
	    end_date

===========================================================================================================================

Site_Maskwa_steam_recommendation:
-----------------------------

	params:
		site_data: for each trunk -> pad : {
			data (csv file / dropped unnamed column)
			max_constraints (well constraints)
			model
			pad_constraints: (pad constraints)
		}
		scenario_id
		step_size: 100
		steam_site_daily_inject: 1100
		steam_site_constraint_daily : 1100
		trunk_constraints: (trunk constraints)
		well_statuses: (well statuses)
		start_date
		end_date

	variables / code:
		steam_site_daily_inject: 1100
		build the following: 
			Pad_infill_well_list: wells per trunk
			pad_constraints : list of (pad, [constraints])
			well_constraints: list of (well, [constraints])
			date_ranges: for each date range add this: 
				(
                    end_date, 
                    start_date, 
                    min_trunk_stream ([0,0,0]), 
                    max_trunk_stream ([20000,20000]), 
                    min_pad_stream: { pad_name: 500, ... },
                    max_pad_stream: { pad_name: 5000, ... }
                )
            for each date_range (usually one):
                trunk_steam_allocation: { for each trunk the bigger between two values: 1. min of each trunk, or 2. sum of min of pads}
                trunk_steam_max_allocation: { for each trunk the smaller between two: 1. the max for trunk, or 2. the sum of max of pads }
			
                Steam_site_daily_inject_local : is the min of two values: 1. the Steam_site_daily_inject or 2. sum of all max of trunk in trunk_steam_max_allocation
                trunk_steam_step = 1000
                max_volume_total = 0
                recomd_steam_distribution = {dict for each trunk an empty array}
                prediction_output_temp = None
                

                check and validate the `trunk_steam_allocation`

                results_cache = { for each trunk }
                initial = trunk_steam_allocation
                trunk_steam_allocation_sum = sum of all trunk steam allocation : trunk_steam_allocation

                | down remark : while sum of all current trunk stream < max steam site which is steam site or sum of all trunks |
                while trunk_steam_allocation_sum < Steam_site_daily_inject_local or initial:
                    print ('.... Running 365 : ', )
                    Do Something (while_loop)
                    update trunk_steam_allocation_sum


        -------------------------------------------------
        Do Somthing (while_loop):
            max_volume_total = 0
            adjust the trunk step size (1000 or lower)

            for each trunk in trunk_steam_allocation:
                temp_sum_total = 0
                recomendation_temp = []
                df_Pad_data_name_date_temp = None
                trunk_steam_allocation_temp = trunk_steam_allocation.copy()
                adjust the trunk_steam_step

                for each trunk in site_data:
                    recomendation_local_temp = None
                    max_volume_temp, recomd_steam_distribution_temp, df_Pad_data_name_date_local, cache, recomendation_local_temp = One_Trunk_steam_recommendation()
                    results_cache[trunk] = cache
                    recomd_steam_distribution[trunk] = recomd_steam_distribution_temp.copy()
                    temp_sum_total += max_volume_temp
                    recomendation_temp += recomendation_local_temp
                    df_Pad_data_name_date_temp = pd.concat([df_Pad_data_name_date_temp, df_Pad_data_name_date_local])



===========================================================================================================================

One_Trunk_steam_recommendation
-----------------------------
    params:
        pad_data:                       as above but only for the trunk
        Steam_step_size:                100
        Steam_site_daily_inject:        3500 (min of trunk)
        Steam_site_constraint_daily:    3500
        Steam_trunk_constraint_daily:   trunk_constraints(for this trunk)
        recomnd_steam_array:            []
        start_date:                     start_date
        end_date:                       end_date
        results_cache                   {}
        Pad_infill_well_list:           list of wells for the trunk
        print_result=1  # print_result = 1 will print recommendation results as well as corresponding oil production
    
    variables / code:
        recomd_steam_distribution = None
        tmp = time.time()
        output_prediction = None
        output_array = []
        volume = 0
        for row in Steam_trunk_constraint_daily (usually on constraint):
            Steam_pad_constraint_daily_max = 0
            Steam_well_constraint_daily_max = []
            infill_well_list = [] (list of all wells (volume columns))
            for pad in pad_data:
                add to the pad_data: {
                    // previous
                    data, max_constraints, model, pad_constraints
                    // new
                    maximum_pad_constraint: last constraint steam
                    minimum_pad_constraint: last constraint min
                    min_well_constraints: [ list of well min constraint ]
                    max_well_constraints: [ list of well max constraint ]
                }
            
            Steam_well_constraint_daily_max = concat well max for all wells
            Steam_well_constraint_all_well_daily_max = sum of list of pad max where pad max = bigger of either 1. pad constraint max 2. sum of well max

            Steam_well_constraint_daily_min = [0] * number of wells
            max_rec = 0
            Total_steam_ava = Steam_site_daily_inject (start point)





        return volume, recomnd_steam_array, output_prediction, results_cache, output_array
