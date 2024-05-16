package com.example.theoryofprobability_practise2

import android.annotation.SuppressLint
import android.icu.lang.UCharacter
import android.os.Bundle
import android.util.DisplayMetrics
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.FloatTweenSpec
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.animateTo
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.theoryofprobability_practise2.ui.theme.TheoryOfProbability_Practise2Theme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.roundToInt

import org.apache.commons.math3.*
import org.apache.commons.math3.analysis.function.Gaussian
import org.apache.commons.math3.distribution.FDistribution
import org.apache.commons.math3.distribution.NormalDistribution
import org.apache.commons.math3.distribution.TDistribution
import java.util.function.Predicate
import kotlin.math.max
import kotlin.math.min

data class FValues(val Fcritical: Double, val Fobservable: Double)
{
    constructor(p: Pair<Double, Double>) : this(p.first, p.second){}

}
data class TValues(val Tcritical: Double, val Tobservable: Double)
{
    constructor(p: Pair<Double, Double>) : this(p.first, p.second){}

}
data class UValues(val Ucritical: Double, val Uobservable: Double)
{
    constructor(p: Pair<Double, Double>) : this(p.first, p.second){}

}

class MyViewModel : ViewModel()
{
    inner class Data(var a: String, var b: Int)

    var sf = mutableStateOf(Data("qwer", 2))
    var taskContents = mutableMapOf<String, @Composable () -> Unit>(
        "Задача 1" to {Task1()},
        "Задача 2" to {Task2()},
        "Задача 3" to {Task3()},
        "Задача 4" to {Task4()},
        "Задача 5" to {Task2()},
        "Задача 6" to {Task6()},
        "Задача 7" to {Task7()}
    )
}

fun get_F_T_Results(
    nX: Int,
    nY: Int,
    mX: Double,
    mY: Double,
    s0X: Double,
    s0Y: Double,
    alpha: Double
) : Pair<FValues, TValues>
{
    var (sNum, sDen) = max(s0X, s0Y) to min(s0X, s0Y)
    var (nFirst, nSecond) = if (s0X != sNum) nX to nY else nY to nX
    val fObserv = sNum / sDen
    val fCrit = 1 /
            FDistribution(
                nFirst.toDouble() - 1,
                nSecond.toDouble() - 1
            ).inverseCumulativeProbability(
                alpha
            )


    val tObserv = Math.abs((mX - mY) / Math.sqrt((nX - 1) * s0X + (nY - 1) * s0Y) * Math.sqrt(nX * nY * (nX + nY - 2).toDouble() / (nX + nY)))
    val tCrit = -TDistribution(nX + nY - 2.0).inverseCumulativeProbability(alpha)
    return FValues(fCrit, fObserv) to TValues(tCrit, tObserv)
}

@Composable
fun ElementColumn(
    modifier: Modifier,
    elementModifier: Modifier,
    values: Array<@Composable () -> Unit>,
    inputElementConditions: List<Predicate<String>> = listOf(),
    onRemoveElement: (Int) -> Unit,
    onAddElement: (String) -> Unit
)
{
    @Composable
    fun NumberCard(modifier: Modifier, content: @Composable () -> Unit, minusClick: () -> Unit)
    {
        Column(
            modifier = Modifier
                .padding(5.dp)
                .then(modifier),
            horizontalAlignment = Alignment.CenterHorizontally
        )
        {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End){
                Image(
                    modifier = Modifier.clickable { minusClick() },
                    painter = painterResource(id = R.drawable.x),
                    contentDescription = null
                )
            }
            content()
        }
    }

    var plusClicked by remember {
        mutableStateOf(false)
    }
    if (plusClicked)
    {
        Dialog(
            onDismissRequest = {
                plusClicked = false
            }
        )
        {
            var inputData = remember {
                mutableStateOf("")
            }
            Column(
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                TextField(
                    value = inputData.value,
                    onValueChange = {
                        if (inputElementConditions.all{cond -> cond.test(it)}){
                            inputData.value = it
                        }
                    },
                    label = {
                        Text("Введите элемент")
                    }
                )
                OutlinedButton(onClick = { onAddElement(inputData.value); plusClicked = false }) {
                    Text("Добавить", color = Color.White)
                }
            }
        }
    }
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .weight(1f)
        )
        {
            values.forEachIndexed {ind, it ->
                NumberCard(elementModifier, it, {onRemoveElement(ind)})
            }
        }
        Divider(modifier = Modifier
            .padding(5.dp),
            thickness = 2.dp)
        Box(
            modifier = Modifier
                .padding(10.dp)
                .clickable {
                    plusClicked = true
                }
                .background(Color.Green)
                .size(20.dp),
            contentAlignment = Alignment.Center
        )
        {
            Text("+", modifier = Modifier.scale(1.5f))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task1(){
    var nxStr = remember {
        mutableStateOf("")
    }
    var nyStr = remember {
        mutableStateOf("")
    }
    var S0xStr = remember {
        mutableStateOf("")
    }
    var S0yStr = remember {
        mutableStateOf("")
    }
    var alpha = remember {
        mutableStateOf("0.01")
    }
    var resultFValues = remember {
        mutableStateOf<FValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(nxStr.value, nyStr.value, S0xStr.value, S0yStr.value, alpha.value){
        var (nX, nY, s0X, s0Y) = listOf(nxStr.value, nyStr.value, S0xStr.value, S0yStr.value).map{it.toDoubleOrNull() ?: 0.0}
        if (nX > 0 && nY > 0 && s0X >= 0 && s0Y >= 0){
            try {
                resultFValues.value = null
                resultFValues.value = get_F_T_Results(nX.toInt(), nY.toInt(), 0.0, 0.0, s0X, s0Y, alpha.value.toDouble()).first
            }
            catch(e: Exception)
            {

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            text = {
                Text("Заполнить значениями")
            },
            onClick = {
                nxStr.value = "17"
                nyStr.value = "9"
                S0xStr.value = "15.82"
                S0yStr.value = "23.66"
                menuExpanded = false
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {
        Text(text = "гипотеза H0: Dx = Dy\nгипотеза Н1: ${try{if (S0xStr.value.toDouble() > S0yStr.value.toDouble()) "Dx > Dy" else "Dy > Dx"} catch(e: Exception){"не определено"}}", modifier = Modifier.align(Alignment.CenterHorizontally))
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            value = alpha.value,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha.value = it
                }
            },
            label = {
                Text("alpha")
            },
            singleLine = true,
        )
        Row(
            modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier
                    .width(150.dp),
                value = nxStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull()?.let{true} ?: false)
                    {
                        nxStr.value = it
                    }
                },
                label = {
                    Text("nX")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(150.dp),
                value = S0xStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        S0xStr.value = it
                    }
                },
                label = {
                    Text("S0X")
                },
                singleLine = true
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier
                    .width(150.dp),
                value = nyStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull()?.let{true} ?: false)
                    {
                        nyStr.value = it
                    }
                },
                label = {
                    Text("nY")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(150.dp),
                value = S0yStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        S0yStr.value = it
                    }
                },
                label = {
                    Text("S0Y")
                },
                singleLine = true
            )
        }
        resultFValues.value?.let{ p ->
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            {
                Row()
                {
                    Text("F кр: ", color = Color.Red)
                    Text(p.Fcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F набл: ", color = Color.Yellow)
                    Text(p.Fobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Вывод: ", color = Color.White)
                    if (p.Fobservable > p.Fcritical){
                        Text("Гипотеза Н0 неверна", color = Color.Red)
                    }
                    else{
                        Text("Гипотеза Н0 верна", color = Color.Green)
                    }
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task2(){
    var xValues = remember {
        mutableStateListOf<Double>()
    }
    var yValues = remember {
        mutableStateListOf<Double>()
    }
    var alpha = remember {
        mutableStateOf("0.05")
    }

    var resultMX by remember {
        mutableStateOf(0.0)
    }
    var resultMY by remember {
        mutableStateOf(0.0)
    }
    var resultDX by remember {
        mutableStateOf(0.0)
    }
    var resultDY by remember {
        mutableStateOf(0.0)
    }
    var resultTValues = remember {
        mutableStateOf<TValues?>(null)
    }
    var resultFValues = remember {
        mutableStateOf<FValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(alpha.value, xValues.size, yValues.size){
        if (xValues.isNotEmpty() && yValues.isNotEmpty()) {
            try {
                resultFValues.value = null
                resultTValues.value = null
                resultMX = xValues.average()
                resultMY = yValues.average()
                resultDX = xValues.run { map { Math.pow(it - resultMX, 2.0) }.sum() / this.size }
                resultDY = yValues.run { map { Math.pow(it - resultMY, 2.0) }.sum() / this.size }
                var s0X = resultDX * xValues.size.run { this / (this - 1) }
                var s0Y = resultDY * xValues.size.run { this / (this - 1) }
                var nX = xValues.size
                var nY = yValues.size

                get_F_T_Results(nX, nY, resultMX, resultMY, s0X, s0Y, alpha.value.toDouble()).let{ res ->
                    resultFValues.value = res.first
                    resultTValues.value = res.second
                }
            }
            catch (e: Exception)
            {

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            text = {
                Text("Заполнить значениями (№ 2)")
            },
            onClick = {
                xValues.clear()
                xValues.addAll("7.8 8.2 9.1 8.9 8.6".split(" ").map{it.toDoubleOrNull() ?: 0.0})

                yValues.clear()
                yValues.addAll("6.6 7.1 6.3 7 6.2 5.8".split(" ").map{it.toDoubleOrNull() ?: 0.0})

                menuExpanded = false
            }
        )
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            text = {
                Text("Заполнить значениями (№ 5)")
            },
            onClick = {
                xValues.clear()
                xValues.addAll("139 137 134 134 137 137 135 137 135 135".split(" ").map{it.toDoubleOrNull() ?: 0.0})

                yValues.clear()
                yValues.addAll("136 136 132 134 136 136 134 132 136 136 136 136".split(" ").map{it.toDoubleOrNull() ?: 0.0})

                menuExpanded = false
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {

        Spacer(Modifier.weight(1f))
        Text(text = "гипотеза H0: Mx = My\nгипотеза Н1: ${if (resultMX >= resultMY) "Mx > My" else "My > Mx"}", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            value = alpha.value,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha.value = it
                }
            },
            label = {
                Text("alpha")
            },
            singleLine = true
        )
        Spacer(Modifier.weight(1f))
        Row(modifier = Modifier
            .weight(4f)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly)
        {
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text("X", color = Color.White)
                ElementColumn(
                    modifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(0f, 0f),
                                    Offset(size.width, size.height),
                                    listOf(Color.Yellow, Color.Magenta)
                                )
                            }
                        })
                        .fillMaxHeight(1f)
                        .width(100.dp),
                    elementModifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(size.width, 0f),
                                    Offset(0f, size.height),
                                    listOf(Color.Green, Color.Cyan)
                                )
                            }
                        })
                        .size(40.dp),
                    values = xValues.map<Double, @Composable () -> Unit> { @Composable {Text(it.toString())} }.toTypedArray(),
                    onRemoveElement = {
                        xValues.removeAt(it)
                    },
                    onAddElement = {
                        xValues += it.toDoubleOrNull() ?: 0.0
                    }
                )
            }
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text("Y", color = Color.White)
                ElementColumn(
                    modifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(0f, 0f),
                                    Offset(size.width, size.height),
                                    listOf(Color.Yellow, Color.Magenta)
                                )
                            }
                        })
                        .fillMaxHeight(1f)
                        .width(100.dp),
                    elementModifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(size.width, 0f),
                                    Offset(0f, size.height),
                                    listOf(Color.Green, Color.Cyan)
                                )
                            }
                        })
                        .size(40.dp),
                    values = yValues.map<Double, @Composable () -> Unit> { @Composable {Text(it.toString())} }.toTypedArray(),
                    onRemoveElement = {
                        yValues.removeAt(it)
                    },
                    onAddElement = {
                        yValues += it.toDoubleOrNull() ?: 0.0
                    }
                )
            }
        }
        Spacer(Modifier.weight(1f))
        resultTValues.value?.let{ p ->
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            {
                Row()
                {
                    Text("Mx: ", color = Color.Blue)
                    Text(resultMX.toString(), color = Color.White)
                }
                Row()
                {
                    Text("My: ", color = Color.Blue)
                    Text(resultMY.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Dx: ", color = Color.Blue)
                    Text(resultDX.toString(), color = Color.White)
                }
                Row()
                {
                    Text("DY: ", color = Color.Blue)
                    Text(resultDY.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F кр: ", color = Color.Red)
                    Text(resultFValues.value?.Fcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F набл: ", color = Color.Yellow)
                    Text(resultFValues.value?.Fobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T кр: ", color = Color.Red)
                    Text(p.Tcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T набл: ", color = Color.Yellow)
                    Text(p.Tobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Вывод: ", color = Color.White)
                    if (resultFValues.value!!.Fobservable <= resultFValues.value!!.Fcritical) {
                        if (p.Tobservable > p.Tcritical) {
                            Text("Гипотеза Н0 неверна", color = Color.Red)
                        } else {
                            Text("Гипотеза Н0 верна", color = Color.Green)
                        }
                    }
                    else{
                        Text("Генеральные Дисперсии не равны, нельзя судить о Генеральных Средних", color = Color.Magenta)
                    }
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(Modifier.weight(1f))
    }
}
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task3(){
    var nxStr = remember {
        mutableStateOf("")
    }
    var nyStr = remember {
        mutableStateOf("")
    }
    var mXStr = remember {
        mutableStateOf("")
    }
    var mYStr = remember {
        mutableStateOf("")
    }
    var S0xStr = remember {
        mutableStateOf("")
    }
    var S0yStr = remember {
        mutableStateOf("")
    }
    var alpha = remember {
        mutableStateOf("0.01")
    }
    var resultFValues = remember {
        mutableStateOf<FValues?>(null)
    }
    var resultTValues = remember {
        mutableStateOf<TValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    LaunchedEffect(alpha.value, nxStr.value, nyStr.value, mXStr.value, mYStr.value, S0xStr.value, S0yStr.value){
        operator fun <E> List<E>.component6(): E {
            return this[5]
        }
        var (nX, nY, mX, mY, s0X, s0Y) = listOf(nxStr.value, nyStr.value, mXStr.value, mYStr.value, S0xStr.value, S0yStr.value).map{it.toDoubleOrNull() ?: 0.0}
        if (nX > 0 && nY > 0 && s0X >= 0 && s0Y >= 0){
            try {
                resultFValues.value = null
                resultTValues.value = null
                get_F_T_Results(nX.toInt(), nY.toInt(), mX, mY, s0X, s0Y, alpha.value.toDouble()).let{ res ->
                    resultFValues.value = res.first
                    resultTValues.value = res.second
                }
            }
            catch (e: Exception)
            {

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            text = {
                Text("Заполнить значениями")
            },
            onClick = {
                nxStr.value = "15"
                nyStr.value = "12"
                mXStr.value = "4.85"
                mYStr.value = "5.07"
                S0xStr.value = (0.94*0.94).toString()
                S0yStr.value = (1.02*1.02).toString()
                menuExpanded = false
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {
        Text(text = "гипотеза H0: Mx = My\nгипотеза Н1: ${if ((mXStr.value.toDoubleOrNull() ?: 0.0) >= (mYStr.value.toDoubleOrNull() ?: 0.0)) "Mx > My" else "My > Mx"}", modifier = Modifier.align(Alignment.CenterHorizontally))
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            value = alpha.value,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha.value = it
                }
            },
            label = {
                Text("alpha")
            },
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = nxStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull()?.let{true} ?: false)
                    {
                        nxStr.value = it
                    }
                },
                label = {
                    Text("nX")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = mXStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        mXStr.value = it
                    }
                },
                label = {
                    Text("mX")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = S0xStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        S0xStr.value = it
                    }
                },
                label = {
                    Text("S0X")
                },
                singleLine = true
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = nyStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toIntOrNull()?.let{true} ?: false)
                    {
                        nyStr.value = it
                    }
                },
                label = {
                    Text("nY")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = mYStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        mYStr.value = it
                    }
                },
                label = {
                    Text("mY")
                },
                singleLine = true
            )
            OutlinedTextField(
                modifier = Modifier
                    .width(100.dp),
                value = S0yStr.value,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        S0yStr.value = it
                    }
                },
                label = {
                    Text("S0Y")
                },
                singleLine = true
            )
        }
        resultTValues.value?.let{ p ->
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            {
                Row()
                {
                    Text("Mx: ", color = Color.Blue)
                    Text(mXStr.value, color = Color.White)
                }
                Row()
                {
                    Text("My: ", color = Color.Blue)
                    Text(mYStr.value, color = Color.White)
                }
                Row()
                {
                    Text("F кр: ", color = Color.Red)
                    Text(resultFValues.value?.Fcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F набл: ", color = Color.Yellow)
                    Text(resultFValues.value?.Fobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T кр: ", color = Color.Red)
                    Text(p.Tcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T набл: ", color = Color.Yellow)
                    Text(p.Tobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Вывод: ", color = Color.White)
                    if (resultFValues.value!!.Fobservable <= resultFValues.value!!.Fcritical) {
                        if (p.Tobservable > p.Tcritical) {
                            Text("Гипотеза Н0 неверна", color = Color.Red)
                        } else {
                            Text("Гипотеза Н0 верна", color = Color.Green)
                        }
                    }
                    else{
                        Text("Генеральные Дисперсии не равны, нельзя судить о Генеральных Средних", color = Color.Magenta)
                    }
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
    }
}


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task4(){
    var xValues = remember {
        mutableStateListOf<Pair<Double, Int>>()
    }
    var yValues = remember {
        mutableStateListOf<Pair<Double, Int>>()
    }
    var alpha = remember {
        mutableStateOf("0.05")
    }

    var resultMX by remember {
        mutableStateOf(0.0)
    }
    var resultMY by remember {
        mutableStateOf(0.0)
    }
    var resultDX by remember {
        mutableStateOf(0.0)
    }
    var resultDY by remember {
        mutableStateOf(0.0)
    }
    var resultTValues = remember {
        mutableStateOf<TValues?>(null)
    }
    var resultFValues = remember {
        mutableStateOf<FValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(alpha.value, xValues.size, yValues.size){
        if (xValues.isNotEmpty() && yValues.isNotEmpty()) {
            try {
                resultFValues.value = null
                resultTValues.value = null
                var nX = xValues.sumOf{it.second}
                var nY = yValues.sumOf{it.second}
                resultMX = xValues.sumOf { it.first * it.second } / nX
                resultMY = yValues.sumOf { it.first * it.second } / nY
                resultDX = xValues.sumOf { Math.pow(it.first - resultMX, 2.0) * it.second } / nX
                resultDY = yValues.sumOf { Math.pow(it.first - resultMY, 2.0) * it.second } / nY
                var s0X = resultDX * nX / (nX - 1)
                var s0Y = resultDY * nY / (nY - 1)

                get_F_T_Results(nX, nY, resultMX, resultMY, s0X, s0Y, alpha.value.toDouble()).let{ res ->
                    resultFValues.value = res.first
                    resultTValues.value = res.second
                }
            }
            catch (e: Exception)
            {

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            modifier = Modifier
                .fillMaxHeight(0.3f),
            text = {
                Text("Заполнить значениями")
            },
            onClick = {
                xValues.clear()
                xValues.addAll("34 35 37 39".split(" ").map{it.toDouble()}.zip("2 3 4 1".split(" ").map{it.toInt()}))

                yValues.clear()
                yValues.addAll("32 34 36".split(" ").map{it.toDouble()}.zip("2 2 8".split(" ").map{it.toInt()}))

                menuExpanded = false
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize(),
        verticalArrangement = Arrangement.SpaceEvenly
    )
    {
        Spacer(Modifier.weight(1f))
        Text(text = "гипотеза H0: Mx = My\nгипотеза Н1: ${if (resultMX >= resultMY) "Mx > My" else "My > Mx"}", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .width(150.dp),
            value = alpha.value,
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha.value = it
                }
            },
            label = {
                Text("alpha")
            },
            singleLine = true
        )
        Spacer(Modifier.weight(1f))
        Row(modifier = Modifier
            .weight(4f)
            .fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly)
        {
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text("X", color = Color.White)
                ElementColumn(
                    modifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(0f, 0f),
                                    Offset(size.width, size.height),
                                    listOf(Color.Yellow, Color.Magenta)
                                )
                            }
                        })
                        .fillMaxHeight(1f)
                        .width(100.dp),
                    elementModifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(size.width, 0f),
                                    Offset(0f, size.height),
                                    listOf(Color.Green, Color.Cyan)
                                )
                            }
                        })
                        .size(40.dp),
                    values = xValues.map<Pair<Double, Int>, @Composable () -> Unit> {
                        @Composable {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text("${it.first}: ")
                                Text("${it.second}", color = Color.Red)
                            }
                        }
                    }.toTypedArray(),
                    onRemoveElement = {
                        xValues.removeAt(it)
                    },
                    onAddElement = {
                        xValues += try{
                            it.split(" ").run{
                                this[0].toDouble() to this[1].toInt()
                            }
                        }
                        catch(e: Exception)
                        {
                            0.0 to 1
                        }
                    }
                )
            }
            Column(modifier = Modifier, horizontalAlignment = Alignment.CenterHorizontally)
            {
                Text("Y", color = Color.White)
                ElementColumn(
                    modifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(0f, 0f),
                                    Offset(size.width, size.height),
                                    listOf(Color.Yellow, Color.Magenta)
                                )
                            }
                        })
                        .fillMaxHeight(1f)
                        .width(100.dp),
                    elementModifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(size.width, 0f),
                                    Offset(0f, size.height),
                                    listOf(Color.Green, Color.Cyan)
                                )
                            }
                        })
                        .size(40.dp),
                    values = yValues.map<Pair<Double, Int>, @Composable () -> Unit> {
                        @Composable {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            )
                            {
                                Text("${it.first}: ")
                                Text("${it.second}", color = Color.Red)
                            }
                        }
                    }.toTypedArray(),
                    onRemoveElement = {
                        yValues.removeAt(it)
                    },
                    onAddElement = {
                        yValues += try{
                            it.split(" ").run{
                                this[0].toDouble() to this[1].toInt()
                            }
                        }
                        catch(e: Exception)
                        {
                            0.0 to 1
                        }
                    }
                )
            }
        }
        Spacer(Modifier.weight(1f))
        resultTValues.value?.let{ p ->
            Column(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
            )
            {
                Row()
                {
                    Text("Mx: ", color = Color.Blue)
                    Text(resultMX.toString(), color = Color.White)
                }
                Row()
                {
                    Text("My: ", color = Color.Blue)
                    Text(resultMY.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Dx: ", color = Color.Blue)
                    Text(resultDX.toString(), color = Color.White)
                }
                Row()
                {
                    Text("DY: ", color = Color.Blue)
                    Text(resultDY.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F кр: ", color = Color.Red)
                    Text(resultFValues.value?.Fcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("F набл: ", color = Color.Yellow)
                    Text(resultFValues.value?.Fobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T кр: ", color = Color.Red)
                    Text(p.Tcritical.toString(), color = Color.White)
                }
                Row()
                {
                    Text("T набл: ", color = Color.Yellow)
                    Text(p.Tobservable.toString(), color = Color.White)
                }
                Row()
                {
                    Text("Вывод: ", color = Color.White)
                    if (resultFValues.value!!.Fobservable <= resultFValues.value!!.Fcritical) {
                        if (p.Tobservable > p.Tcritical) {
                            Text("Гипотеза Н0 неверна", color = Color.Red)
                        } else {
                            Text("Гипотеза Н0 верна", color = Color.Green)
                        }
                    }
                    else{
                        Text("Генеральные Дисперсии не равны, нельзя судить о Генеральных Средних", color = Color.Magenta)
                    }
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task6(){
    var alpha by remember{
        mutableStateOf("")
    }
    var hypoteticProbabilityStr by remember {
        mutableStateOf("")
    }
    var nStr by remember {
        mutableStateOf("")
    }
    var mStr by remember {
        mutableStateOf("")
    }
    var resultUValues = remember {
        mutableStateOf<UValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    //here is a left-sided critical place(bcs we check if p < p0 (more efficient))
    LaunchedEffect(alpha, nStr, mStr, hypoteticProbabilityStr){
        var (n, m, p0) = listOf(nStr, mStr, hypoteticProbabilityStr).map{it.toDoubleOrNull() ?: 0.0}
        if (n > 0 && m > 0) {
            try {
                resultUValues.value = null
                var uObs = (m/n - p0) * Math.sqrt(n) / Math.sqrt(p0 * (1-p0))
                var uCrit = -NormalDistribution(0.0, 1.0).inverseCumulativeProbability(1 - alpha.toDouble())
                resultUValues.value = UValues(uCrit, uObs)
            }
            catch (e: Exception){

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            text = {
                Text("Подставить значения (№6)")
            },
            onClick = {
                alpha = "0.05"
                hypoteticProbabilityStr = "0.08"
                nStr = "1000"
                mStr = "100"
            }
        )
        DropdownMenuItem(
            text = {
                Text("Подставить значения (№6')")
            },
            onClick = {
                alpha = "0.05"
                hypoteticProbabilityStr = "0.08"
                nStr = "1500"
                mStr = "100"
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    )
    {
        Spacer(Modifier.weight(1f))
        Text("гипотеза Н0: p = p0\nгипотеза Н1: p < p0", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            value = alpha.toString(),
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha = it
                }
            },
            label = {
                Text("alpha")
            }
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = hypoteticProbabilityStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        hypoteticProbabilityStr = it
                    }
                },
                label = {
                    Text("p0")
                }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = mStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        mStr = it
                    }
                },
                label = {
                    Text("m")
                }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = nStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        nStr = it
                    }
                },
                label = {
                    Text("n")
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        resultUValues.value?.let{
            Row(

            )
            {
                Text("U крит: ", color = Color.Red)
                Text(it.Ucritical.toString())
            }
            Row(

            )
            {
                Text("U набл: ", color = Color.Yellow)
                Text(it.Uobservable.toString())
            }
            Row(
            )
            {
                Text("Вывод: ")// тк левосторонняя кр об
                if (resultUValues.value!!.run{Uobservable > Ucritical}){
                    Text("Н0 принимается, нет оснований утверждать, что новые технологии лучше", color = Color.Green)
                }
                else{
                    Text("Н0 отвергается, новые технологии лучше", color = Color.Red)
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Task7(){
    var alpha by remember{
        mutableStateOf("")
    }
    var hypoteticProbabilityStr by remember {
        mutableStateOf("")
    }
    var nStr by remember {
        mutableStateOf("")
    }
    var mStr by remember {
        mutableStateOf("")
    }
    var resultUValues = remember {
        mutableStateOf<UValues?>(null)
    }
    var menuExpanded by remember {
        mutableStateOf(false)
    }

    //here is a left-sided critical place(bcs we check if p < p0 (more efficient))
    LaunchedEffect(alpha, nStr, mStr, hypoteticProbabilityStr){
        var (n, m, p0) = listOf(nStr, mStr, hypoteticProbabilityStr).map{it.toDoubleOrNull() ?: 0.0}
        if (n > 0 && m > 0) {
            try {
                resultUValues.value = null
                var uObs = (m/n - p0) * Math.sqrt(n) / Math.sqrt(p0 * (1-p0))
                var uCrit = NormalDistribution(0.0, 1.0).inverseCumulativeProbability(1 - alpha.toDouble())
                resultUValues.value = UValues(uCrit, uObs)
            }
            catch (e: Exception){

            }
        }
    }

    DropdownMenu(
        expanded = menuExpanded,
        onDismissRequest = {
            menuExpanded = false
        }
    )
    {
        DropdownMenuItem(
            text = {
                Text("Подставить значения")
            },
            onClick = {
                alpha = "0.01"
                hypoteticProbabilityStr = "0.01"
                nStr = "400"
                mStr = "5"
            }
        )
    }
    Column(
        modifier = Modifier
            .combinedClickable(
                onLongClick = {
                    menuExpanded = true
                },
                onClick = {

                },
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
            .fillMaxSize()
    )
    {
        Spacer(Modifier.weight(1f))
        Text("гипотеза Н0: p = p0\nгипотеза Н1: p > p0", modifier = Modifier.align(Alignment.CenterHorizontally))
        Spacer(Modifier.weight(1f))
        OutlinedTextField(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            value = alpha.toString(),
            onValueChange = {
                if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                {
                    alpha = it
                }
            },
            label = {
                Text("alpha")
            }
        )
        Spacer(Modifier.weight(1f))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        )
        {
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = hypoteticProbabilityStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        hypoteticProbabilityStr = it
                    }
                },
                label = {
                    Text("p0")
                }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = mStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        mStr = it
                    }
                },
                label = {
                    Text("m")
                }
            )
            OutlinedTextField(
                modifier = Modifier.width(100.dp),
                value = nStr,
                onValueChange = {
                    if (it.isEmpty() || it.toDoubleOrNull()?.let{true} ?: false)
                    {
                        nStr = it
                    }
                },
                label = {
                    Text("n")
                }
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        resultUValues.value?.let{
            Row(

            )
            {
                Text("U крит: ", color = Color.Red)
                Text(it.Ucritical.toString())
            }
            Row(

            )
            {
                Text("U набл: ", color = Color.Yellow)
                Text(it.Uobservable.toString())
            }
            Row(
            )
            {
                Text("Вывод: ")// тк левосторонняя кр об
                if (resultUValues.value!!.run{Uobservable <= Ucritical}){
                    Text("Н0 принимается, партию можно принять", color = Color.Green)
                }
                else{
                    Text("Н0 отвергается, партия бракована", color = Color.Red)
                }
            }
        } ?:
        let{
            Text("нет данных", modifier = Modifier.align(Alignment.CenterHorizontally))
        }
        Spacer(modifier = Modifier.weight(1f))
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun App(){
    var vm: MyViewModel = viewModel()
    var thisScope = rememberCoroutineScope()
    var screenSize = remember{ mutableStateOf(Size(100f, 100f)) }

    val taskContents = vm.taskContents
    var draggableState = remember {
        AnchoredDraggableState(
            initialValue = 1,
            anchors = DraggableAnchors {
                1 at 0f
                2 at -100f
                3 at -200f
            },
            positionalThreshold = { d: Float ->
                200f
            },
            velocityThreshold = {
                10f
            },
            animationSpec = FloatTweenSpec(500)
        )
    }
    LaunchedEffect(vm.taskContents.size) {
        draggableState.updateAnchors(DraggableAnchors {
            repeat(taskContents.size) { ind ->
                (ind + 1) at (-ind * screenSize.value.width)
            }
        })
    }

    Column(
        modifier = Modifier
            .onSizeChanged {
                screenSize.value = it.toSize()
            }
            .fillMaxSize()
    )
    {
        Row(
            modifier = Modifier
                .background(Color.DarkGray)
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        )
        {
            taskContents.entries.forEachIndexed { ind, task ->
                Text(modifier = Modifier
                    .clickable {
                        thisScope.launch {
                            draggableState.animateTo(ind + 1)
                        }
                    }
                    .padding(10.dp), text = task.key, color = if (draggableState.currentValue == ind + 1) Color.Magenta else Color.White)
            }
        }
        Divider(thickness = 2.dp, color = Color.Black)
        Row(
            modifier = Modifier
                .anchoredDraggable(
                    state = draggableState,
                    orientation = Orientation.Horizontal,
                    enabled = true
                )
                .weight(1f)
                .fillMaxWidth()
                .horizontalScroll(ScrollState(-draggableState.offset.roundToInt()), enabled = false)
        )
        {
            taskContents.entries.forEach {
                Box(
                    modifier = Modifier
                        .background(object : ShaderBrush() {
                            override fun createShader(size: Size): Shader {
                                return LinearGradientShader(
                                    Offset(0f, 0f),
                                    Offset(size.width, size.height),
                                    listOf(Color.LightGray, Color.DarkGray)
                                )
                            }
                        })
                        .fillMaxHeight()
                        .width((screenSize.value.width / LocalDensity.current.density).dp),
                    contentAlignment = Alignment.Center
                )
                {
                    it.value()
                }
            }
        }
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}
