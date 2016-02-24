<?php
   echo "Olá Mundo";
   $randomico = rand(0,10);
   if ($randomico >= 7)
      {
	echo "Aprovado";
        parabens();
      }
   else
      echo "Reprovado";	


function parabens(){
    echo "Parabens!";
}   
	
?>