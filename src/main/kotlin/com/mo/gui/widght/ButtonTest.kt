package com.mo.gui.widght

class ButtonTest(x : Int, y :Int,  width : Int, height : Int) : Button(x,y,width,height) {
    override fun onDrag(mouseX: Double, mouseY: Double, deltaX: Double, deltaY: Double) {
        print(mouseX)
        super.onDrag(mouseX, mouseY, deltaX, deltaY)
    }

    override fun onClick(mouseX: Double, mouseY: Double) {
        print("666")
        super.onClick(mouseX, mouseY)
    }






}