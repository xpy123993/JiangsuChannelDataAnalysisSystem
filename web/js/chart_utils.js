/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


function get_chart_name(data_type){
    
    
    switch(data_type % 3){
        case 0:
            return ["���Ŵ���", "��������"];
        case 1:
            return ["��ֻ����", "��ֻ������"];
        case 2:
            return ["����ƽ����բʱ��", "����ƽ����բʱ��"];
    }
}