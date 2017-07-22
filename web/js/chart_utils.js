/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function get_chart_name(data_type){
    
    
    switch(data_type % 3){
        case 0:
            return ["开放次数", "开放天数"];
        case 1:
            return ["船只数量", "船只总载重"];
        case 2:
            return ["上行平均过闸时间", "下行平均过闸时间"];
    }
}