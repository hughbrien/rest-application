#!/usr/bin/env bash
  pingaddress=8.8.8.8
  metricpath="Custom Metrics|Hardware Resources|PingValues"
parse_result(){
  local withtime
  local result
  local rounded
  local output

  rounded=1;
  
  #echo $metricpath;

  while read line; do
  if [[ ! ${line} =~ ^[\+\| ]]; then
    # echo 'in first if, line is '${line}
    if [[ ${line} =~ time=[0-9.]+ ]]; then
      # echo 'in second if line is '${BASH_REMATCH[0]};
      withtime=${BASH_REMATCH[0]};
      if [[ $withtime =~ [0-9]+ ]]; then
      #  echo 'in third if time is '${BASH_REMATCH[0]}
        result=${BASH_REMATCH[0]};
        # echo 'actual result is '$result;
        rounded=$((1+result))
        # echo 'rounded result is '$rounded;
        echo $metricpath',value='$rounded;
      fi
    fi
  fi
done

}
#echo "ping $pingaddress"
parse_result < <(ping $pingaddress -i 60)
