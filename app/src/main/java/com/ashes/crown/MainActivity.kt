package com.ashes.crown

import android.os.Bundle
import android.media.MediaPlayer
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalConfiguration

private val Night = Color(0xFF17110E)
private val Leather = Color(0xFF302118)
private val Gold = Color(0xFFC6A05C)
private val Paper = Color(0xFFE9D9B8)
private val Ink = Color(0xFF352519)
private val Muted = Color(0xFF80633D)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        @Suppress("DEPRECATION")
        window.decorView.systemUiVisibility = (android.view.View.SYSTEM_UI_FLAG_FULLSCREEN
            or android.view.View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            or android.view.View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY)
        setContent { MaterialTheme { AshesGame(this) } }
    }
}

private data class Scene(val chapter: String, val title: String, val place: String, val text: String, val choices: List<GameChoice>)

private val scenes = listOf(
    Scene("ПРОЛОГ · СМЕРТЬ КОРОЛЯ", "Корона без короля", "Замок Эстерваль · рассвет", "Эстерваль. Год 1327. Король Альдрик III мёртв. Его нашли в запертой спальне, а на груди лежала чёрная роза — знак, исчезнувший из летописей триста лет назад.\n\nТы — Эдриан Вейл, младший хранитель королевских архивов. Прошлой ночью король передал тебе ключ и прошептал: «Когда погаснет последняя свеча, не верь тому, кто наденет корону».\n\nТеперь двери дворца заперты. Совет собирается через час. Капитан Гаррет Морн ждёт у архивной лестницы.", listOf(
        GameChoice("Передать капитану найденный свиток", "Доверие королевской гвардии", "Гаррет принимает свиток, но его лицо мрачнеет. На восковой печати — знак, которого нет ни в одном официальном реестре.", reputation=1, guard=12, crown=4, nextScene=1),
        GameChoice("Спрятать свиток в тайнике архива", "Скрыть улику и сохранить свободу действий", "Ты прячешь свиток за переплётом старой летописи. Между страниц обнаруживается записка: «Ищи наследника там, где река входит в камень». ", reputation=2, gold=2, rose=3, nextScene=2),
        GameChoice("Найти леди Селесту в зимнем саду", "Попытаться заключить опасный союз", "Леди Селеста слушает тебя молча. На её пальце блестит кольцо с чёрной розой. Она знает больше, чем говорит.", seleste=14, influence=2, rose=5, nextScene=3),
        GameChoice("Пойти на заседание королевского совета", "Услышать позиции великих домов", "Герцог Рейнар требует немедленной коронации. Селеста возражает: наследник исчез три дня назад. Совет раскалывается на два лагеря.", reputation=2, influence=4, crown=2, commoners=-1, nextScene=4)
    )),
    Scene("ГЛАВА I · ВЕРНОСТЬ", "Печать капитана", "Оружейная башня", "Гаррет запирает дверь и кладёт свиток на стол. «Я служил королю двадцать лет. Если эта печать настоящая, значит, приказы отдавали от имени мёртвого человека ещё до его смерти».\n\nКапитан предлагает сделку: ты помогаешь ему найти источник подделки, а он обеспечивает тебе безопасный проход по замку. Но доверие стражи — не то же самое, что дружба.", listOf(
        GameChoice("Согласиться работать вместе", "Союз с гвардией", "Гаррет протягивает руку. Отныне часть стражников будет сообщать тебе о подозрительных передвижениях.", reputation=1, guard=10, nextScene=5),
        GameChoice("Потребовать письменную гарантию", "Осторожная сделка", "Капитан усмехается, но ставит подпись под короткой грамотой. Он уважает твою осторожность, хотя доверяет не полностью.", influence=2, guard=4, nextScene=5),
        GameChoice("Отказаться и сохранить независимость", "Не связывать себя обещаниями", "Гаррет убирает свиток. «Тогда не стой у меня на пути». Ты уходишь, чувствуя на спине его тяжёлый взгляд.", reputation=-1, nextScene=5)
    )),
    Scene("ГЛАВА I · ТАЙНА", "Чернила под переплётом", "Запретный архив", "Ты возвращаешься к тайнику. На внутренней стороне обложки выдавлены семь имён — названия великих домов. Одно имя зачёркнуто так сильно, что пергамент почти прорван.\n\nВнизу страницы есть схема старого водостока, ведущего к реке. Кто-то оставил тебе путь из замка — или ловушку.", listOf(
        GameChoice("Изучить шифр и карту", "Потратить время на разгадку", "За несколькими слоями чернил обнаруживается герб исчезнувшего дома. Ты запоминаешь расположение тайного хода.", influence=3, nextScene=5),
        GameChoice("Покинуть замок через водосток", "Рискнуть ради свободы", "Камни скользкие, вода ледяная. Ты выбираешься к реке, но теряешь часть припасов.", health=-8, gold=-2, nextScene=5),
        GameChoice("Подбросить ложную улику в покои Рейнара", "Опасная политическая игра", "Слухи о герцоге начинают распространяться. Ты выиграл время, но если обман раскроют, враг будет беспощаден.", reputation=-2, influence=5, nextScene=5)
    )),
    Scene("ГЛАВА I · СОЮЗ", "Леди в серебряной вуали", "Зимний сад", "Селеста касается кольца с чёрной розой. «Во дворце правду не находят, Эдриан. Её выбирают — и платят за неё».\n\nОна предлагает доступ к переписке великих домов, но просит взамен одну услугу: выяснить, кто подменил страницу в книге наследования.", listOf(
        GameChoice("Показать ей копию печати", "Открытость и доверие", "Селеста впервые улыбается. Она узнаёт символ и обещает познакомить тебя с человеком, который видел его раньше.", seleste=12, reputation=1, nextScene=5),
        GameChoice("Спросить прямо о её кольце", "Давление вместо дипломатии", "Её улыбка исчезает. «Любопытство — опасная привычка». Она не прогоняет тебя, но разговор окончен.", seleste=-5, influence=1, nextScene=5),
        GameChoice("Предложить обмен архивной тайной", "Переговоры на равных", "Селеста принимает условия. Теперь у вас есть общий секрет — и причина опасаться друг друга.", seleste=8, gold=3, nextScene=5)
    )),
    Scene("ГЛАВА I · СОВЕТ", "Семь голосов", "Зал великого совета", "Под сводами зала спорят представители великих домов. Герцог Рейнар говорит о порядке, Селеста — о законном наследовании. Старые союзники уже не смотрят друг другу в глаза.\n\nТы замечаешь, что один из лордов носит перстень с тем же знаком, что был на свитке.", listOf(
        GameChoice("Публично потребовать расследования", "Выступить перед знатью", "Голоса стихают. Ты привлёк внимание всего совета — и нажил влиятельных противников.", reputation=4, influence=3, commoners=5, crown=-2, nextScene=5),
        GameChoice("Незаметно проследить за лордом", "Собрать доказательства", "Ты покидаешь зал следом за незнакомцем. Он направляется к старому мосту за городскими стенами.", influence=2, nextScene=5),
        GameChoice("Поддержать предложение о временном регентстве", "Выбрать сторону порядка", "Рейнар благодарит тебя. Но Селеста смотрит так, словно запомнила этот момент навсегда.", reputation=1, guard=2, seleste=-4, nextScene=5)
    )),
    Scene("ГЛАВА II · ГОРОД", "Город просыпается", "Нижние ворота Эстервала", "За стенами замка шумит рынок. В городе уже знают о смерти короля, но каждый рассказывает свою версию. Торговцы боятся закрытия дорог, ремесленники — новых налогов, а нищие шепчут о человеке, которого видели у старого моста.\n\nПеред тобой три пути. От твоего выбора зависит, какие двери откроются дальше.", listOf(
        GameChoice("Отправиться к старому мосту", "След наследника", "У реки ты находишь свежие следы конского копыта и обрывок синего плаща. Кто-то недавно ждал здесь важного гостя.", influence=2, nextScene=6),
        GameChoice("Расспросить торговцев на рынке", "Слухи и связи", "Торговка специями рассказывает о ночной поставке в замок. За сведения она просит небольшую плату.", gold=-3, reputation=2, merchants=5, commoners=2, nextScene=7),
        GameChoice("Посетить особняк одного из великих домов", "Политическое влияние", "Тебя принимают не сразу. Но управляющий соглашается передать письмо лорду, если ты докажешь свою полезность.", influence=5, nextScene=8)
    )),
    Scene("ГЛАВА II · РЕКА", "След у старого моста", "Старый мост", "Под мостом скрывается каменная дверь. На ней семь углублений для печатей, но одно из них покрыто свежей сажей. Из темноты доносится тихий стук — три раза, пауза, ещё два.\n\nТы понимаешь: это не просто тайник. Это место встречи людей, которые не хотят, чтобы их видели при свете дня.", listOf(
        GameChoice("Ответить условным стуком", "Попытаться войти мирно", "За дверью слышится движение. Незнакомый голос спрашивает пароль, которого ты не знаешь.", health=-2, influence=2, nextScene=9),
        GameChoice("Осмотреть каменную кладку", "Найти скрытый механизм", "В шве кладки ты находишь маленький медный ключ с гербом без короны.", gold=1, itemReward="Медный ключ без короны", nextScene=9),
        GameChoice("Вернуться в город и подготовиться", "Не рисковать без сведений", "Ты отходишь от моста. Теперь ты знаешь место, но неизвестные могли заметить тебя.", reputation=1, nextScene=9)
    )),
    Scene("ГЛАВА II · РЫНОК", "Цена слухов", "Рыночная площадь", "Торговка показывает тебе список поставок. Среди обычных товаров значатся семь ящиков чёрного воска — материала, которым запечатывали королевские приказы.\n\nОна предлагает продать тебе копию накладной. Это может стать доказательством, если найти того, кто оплатил груз.", listOf(
        GameChoice("Купить копию накладной", "Заплатить за улику", "Ты получаешь документ с печатью портового склада. След ведёт к Веймару.", gold=-4, influence=3, merchants=3, rose=2, itemReward="Копия портовой накладной", nextScene=9),
        GameChoice("Уговорить торговку помочь бесплатно", "Использовать убеждение", "Она соглашается, если ты пообещаешь защитить её лавку от новых поборов.", reputation=2, nextScene=9),
        GameChoice("Отказаться от сделки", "Сохранить золото", "Ты уходишь без документа. Но запоминаешь имя складского писаря.", nextScene=9)
    )),
    Scene("ГЛАВА II · ДОМ", "За закрытыми ставнями", "Особняк великого дома", "Управляющий проводит тебя в комнату без окон. На столе лежит карта королевства и семь фигурок из чёрного дерева. Одна фигурка уже перевёрнута.\n\n«Нам нужен человек, который умеет читать старые договоры», — говорит управляющий. «Но прежде чем говорить о союзе, докажи, что умеешь хранить тайны».", listOf(
        GameChoice("Согласиться прочесть древний договор", "Показать знания архивариуса", "Ты находишь в договоре скрытый пункт о праве убежища. Управляющий впечатлён.", influence=4, reputation=1, nextScene=9),
        GameChoice("Спросить, кто перевернул фигурку", "Проявить политическую смелость", "Управляющий закрывает карту. «Ты задаёшь правильные вопросы не в то время».", influence=1, nextScene=9),
        GameChoice("Отказаться от сомнительного союза", "Сохранить независимость", "Тебя провожают без угроз, но двери этого дома теперь будут открываться не так легко.", reputation=1, nextScene=9)
    )),
    Scene("ГЛАВА III · НОВАЯ УЛИКА", "Семь печатей", "Тайное убежище", "К концу дня у тебя появляется несколько нитей, ведущих к одной тайне. Чёрная роза — не знак убийцы. Это знак старого договора, заключённого семью домами до основания Эстервала.\n\nКто-то пытается стереть имя настоящего наследника. Но пока ты не знаешь, кому выгодно вернуть его — и кому выгодно, чтобы он никогда не появился.", listOf(
        GameChoice("Продолжить расследование вместе с Гарретом", "Опора на гвардию", "Гаррет обещает людей и доступ к караульным журналам.", guard=8, nextScene=10),
        GameChoice("Попросить помощи у Селесты", "Опора на двор", "Селеста обещает устроить встречу с посланником одного из домов.", seleste=8, nextScene=10),
        GameChoice("Начать собственную сеть осведомителей", "Независимая игра", "Ты нанимаешь двух городских мальчишек собирать слухи. Это дорого, но теперь у тебя есть глаза на улицах.", gold=-5, influence=6, nextScene=10)
    )),
    Scene("ГЛАВА III · ПЕРЕДЫШКА", "Перед бурей", "Комната над трактиром", "Ночь опускается на Эстерваль. Ты раскладываешь найденные бумаги. На рассвете приходит гонец с севера: в деревне Серого Брода пропали запасы зерна, а на дороге замечены вооружённые люди. На одном из обрывков карты стоит печать Вальдена — крепости, которую считали заброшенной.\n\nРасследование смерти короля неожиданно выводит за городские стены. Кто-то проверяет, насколько быстро новое правительство сможет защитить собственные земли.", listOf(
        GameChoice("Отправиться на север вместе с Гарретом", "Путь гвардии: безопасность и сила", "Гаррет собирает небольшой отряд. Его люди знают дорогу, но военное присутствие может напугать жителей Серого Брода.", guard=5, reputation=1, nextScene=11),
        GameChoice("Принять помощь посланника Селесты", "Путь двора: переговоры и тайные сведения", "Посланник передаёт тебе старую грамоту с печатью Вальдена. Селеста просит не обнажать меч раньше времени.", seleste=5, influence=2, nextScene=12),
        GameChoice("Отправиться налегке и скрытно", "Самостоятельный путь", "Ты оставляешь гербовые знаки и нанимаешь проводника. Дорога будет опаснее, зато никто не узнает, кто прибыл в деревню.", gold=-2, rose=2, nextScene=11)
    )),
    Scene("ГЛАВА III · СЕВЕРНЫЙ ТРАКТ", "Дорога через Серый Брод", "Северные владения · тракт", "К полудню тракт сужается между соснами, и отряд замедляет шаг. У обочины лежит перевёрнутая телега: ремни перерезаны, мешки с зерном исчезли, а в грязи отпечатались следы нескольких повозок. Чуть поодаль Гаррет поднимает детский башмак и долго рассматривает его, прежде чем тихо произнести: «Это не случайный налёт. Людей выводили отсюда по приказу».\n\nНа влажной глине виден знак колеса, перечёркнутый тремя короткими линиями. Томас узнаёт его по портовым накладным: так помечали грузы, которые не должны были попасть в королевские книги. Значит, пропавшее зерно связано не только с голодом в деревне — кто-то стирает следы целой тайной поставки.\n\nВпереди показывается Серый Брод. Из труб поднимается тонкий дым, но колокол молчит, ворота закрыты, а на частоколе нет ни одного дозорного. Гаррет просит не торопиться: если жители прячутся от королевской стражи, открытое появление отряда может превратить спасение в новую беду.", listOf(
        GameChoice("Подойти открыто и назвать имя короля", "Показать законную власть", "Староста выходит навстречу, но требует доказательств, что королевская власть ещё существует.", reputation=2, crown=2, nextScene=13),
        GameChoice("Обойти деревню и найти следы похитителей", "Начать с разведки", "В лесу ты находишь тележный след, ведущий к старой мельнице. На деревню напали не ради золота — искали списки поставок.", influence=2, rose=2, nextScene=13),
        GameChoice("Отдать деревне часть своих припасов", "Заслужить доверие жителей", "Люди впервые открывают ворота. Староста рассказывает, что нападавшие спрашивали о королевском архиве и семи печатях.", gold=-3, commoners=6, reputation=2, nextScene=13)
    )),
    Scene("ГЛАВА III · КРИЗИС", "Хлеб и железо", "Деревня Серый Брод", "В амбаре почти не осталось зерна. Староста обвиняет сборщиков податей, мельник — вооружённых людей с чёрными лентами, а жители требуют немедленно наказать виновных. Гаррет настаивает на арестах. Посланник Селесты предлагает сначала выяснить, кто оплатил нападение.\n\nТы находишь в книге поставок подпись королевского казначея. Дата стоит за неделю до смерти Альдрика III.", listOf(
        GameChoice("Защитить амбар и распределить остатки зерна", "Сначала спасти людей", "Ты вводишь строгий учёт и отдаёшь часть дорожного запаса детям и старикам. Недовольство стихает, но отряд теряет время.", commoners=8, health=3, nextScene=14),
        GameChoice("Задержать сборщиков податей", "Потребовать ответа от власти", "Сборщики оказываются лишь исполнителями. Один из них признаётся, что приказы шли из Вальдена и были скреплены чёрным воском.", reputation=2, influence=3, crown=-2, nextScene=14),
        GameChoice("Использовать книгу поставок как приманку", "Рискнуть ради доказательств", "Ты распространяешь слух, что нашёл полный список сообщников. Ночью к амбару приходит человек в плаще с гербом Вальдена.", rose=5, influence=2, health=-4, nextScene=14)
    )),
    Scene("ГЛАВА III · ВАЛЬДЕН", "Огни заброшенной крепости", "Крепость Вальден", "На рассвете вы достигаете Вальдена. Над башнями поднимается дым, хотя крепость числится пустующей уже двенадцать лет. На воротах — свежая королевская печать, а рядом вырезана чёрная роза.\n\nЗа стенами слышны голоса. Это не армия, а небольшой гарнизон, охраняющий архивные ящики. На одном из них ты узнаёшь знак из спальни покойного короля.", listOf(
        GameChoice("Потребовать открыть ворота от имени короны", "Законный вызов", "Стража колеблется. Капитан крепости просит предъявить грамоту наследника.", crown=3, reputation=2, nextScene=15),
        GameChoice("Найти тайный вход по старой карте", "Тихое проникновение", "Ты находишь ход под часовней. Внутри лежит список приказов, подписанных именем умершего короля.", influence=4, rose=4, nextScene=15),
        GameChoice("Предложить гарнизону амнистию в обмен на правду", "Переговоры без боя", "Командир снимает перчатку и показывает клеймо казначейской службы. Он готов говорить, если деревню оставят в покое.", reputation=3, commoners=3, gold=-2, nextScene=15)
    )),
    Scene("ГЛАВА III · ПОСЛЕДСТВИЯ", "Цена первого шага", "Крепость Вальден · внутренний двор", "В руках у тебя оказывается часть королевского архива. Документы доказывают, что приказы от имени Альдрика III продолжали отправлять после его смерти — но имя того, кто стоял за подделкой, вымарано.\n\nС севера пришло известие: Серый Брод ждёт решения, а в столице уже спорят, кто дал тебе право действовать от имени короны. Ты можешь вернуться с доказательствами, закрепить поддержку жителей или продолжить поиски настоящего наследника.", listOf(
        GameChoice("Вернуться в Эстерваль и представить документы совету", "Открыть расследование публично", "Ты выбираешь законный путь. Совет будет вынужден выслушать тебя, но заговорщики узнают, насколько близко ты подошёл к разгадке.", reputation=3, influence=3, nextScene=15),
        GameChoice("Остаться на севере и защитить Серый Брод", "Поставить людей выше двора", "Ты оставляешь часть отряда в деревне и обещаешь расследовать поставки. Жители запомнят, кто пришёл к ним не за налогами.", commoners=6, guard=-2, nextScene=15),
        GameChoice("Тайно отправить копии документов Селесте", "Сохранить козырь в тайне", "Ты прячешь оригиналы и отправляешь копию через доверенного гонца. Теперь у Селесты есть причина помочь тебе — и возможность использовать документы против тебя.", seleste=4, rose=3, nextScene=15)
    )),
    Scene("ГЛАВА IV · СОВЕТ", "Совет под пеплом", "Эстерваль · малый зал совета", "Возвращение в столицу не приносит облегчения. На столе уже лежит указ о чрезвычайном регентстве, а печать под ним принадлежит казначею, чьё имя стоит в северных накладных. Рейнар требует немедленно передать ему архив. Селеста молчит, Гаррет ждёт твоего приказа. Ты понимаешь: документы — не доказательство сами по себе. Важно, кто подтвердит их подлинность и кто услышит правду первым.", listOf(
        GameChoice("Потребовать открытого заседания и вызвать казначея", "Законность и публичное давление", "Ты кладёшь копии приказов перед советом и требуешь сверить печати. Несколько лордов отступают от Рейнара, но кто-то успевает предупредить казначея.", reputation=4, crown=2, influence=2, nextScene=16),
        GameChoice("Попросить Гаррета задержать казначея до допроса", "Ставка на гвардию", "Гаррет соглашается действовать без шума. Он предупреждает: если приказ окажется незаконным, отвечать придётся и тебе.", guard=6, reputation=-1, nextScene=17),
        GameChoice("Передать часть архива Селесте для проверки", "Ставка на тайную дипломатию", "Селеста узнаёт почерк королевского писца и находит след второго экземпляра указа. Она не обещает верности, но впервые называет тебя союзником.", seleste=6, rose=3, nextScene=17)
    )),
    Scene("ГЛАВА IV · СВИДЕТЕЛИ", "Голос за закрытой дверью", "Королевская канцелярия", "Казначей не появляется на заседании. В канцелярии остаётся только его помощник — молодой писарь, который дрожащими руками показывает журнал выдачи печатей. В ночь смерти короля одну из печатей забрали по приказу, подписанному рукой самого казначея. Но писарь видел и второго человека: того, кто принёс приказ и не оставил имени.", listOf(
        GameChoice("Обеспечить писарю защиту и дать показания совету", "Сохранить живого свидетеля", "Гаррет выводит писаря через караульный двор. Его показания подтверждают подлог, однако лицо второго посетителя остаётся неизвестным.", guard=3, commoners=2, reputation=2, nextScene=17),
        GameChoice("Спрятать писаря и изучить журнал самостоятельно", "Сберечь свидетеля от расправы", "Ты находишь отметку о выдаче старой печати дома, исчезнувшего три века назад. Писарь исчезает до рассвета — добровольно или нет, неизвестно.", influence=4, rose=3, nextScene=17),
        GameChoice("Объявить журнал подделкой и проследить за реакцией", "Ложь как приманка", "Ты публично отвергаешь журнал. В тот же вечер неизвестный пытается проникнуть в архив, и теперь у тебя есть возможность взять его с поличным.", rose=5, health=-2, nextScene=17)
    )),
    Scene("ГЛАВА IV · РАЗВЯЗКА", "Имя за семью печатями", "Подземный архив Эстервала", "Три дороги приводят тебя к одной двери — в подземный архив, куда не ступала нога хранителя со времён основания королевства. На каменной плите семь углублений. В центре вырезана корона без драгоценностей, а под ней — незнакомое имя. Селеста узнаёт его и впервые теряет самообладание: это имя было вычеркнуто из родословной не после мятежа, а до него. Значит, история королевского дома построена на лжи. За дверью слышны шаги.", listOf(
        GameChoice("Открыть дверь и встретить неизвестного", "Рискнуть ради истины", "Ты поворачиваешь ключ. В темноте кто-то произносит твоё имя — так, словно ждал тебя много лет.", reputation=2, influence=3, nextScene=18),
        GameChoice("Запечатать вход и сохранить тайну до сбора союзников", "Подготовиться к следующему шагу", "Ты закрываешь плиту и распределяешь копии документов между теми, кому ещё доверяешь. Заговор не раскрыт, но теперь ты знаешь, где искать его сердце.", guard=2, seleste=2, rose=2, nextScene=19),
        GameChoice("Позволить Селесте первой войти в архив", "Проверить её истинные намерения", "Селеста переступает порог и исчезает в темноте. Через мгновение дверь закрывается сама. На камне остаётся её кольцо с чёрной розой.", seleste=-3, rose=6, nextScene=20)
    )),
    Scene("ЭПИЛОГ · ОТКРЫТАЯ ДВЕРЬ", "Наследник из тени", "Подземный архив Эстервала", "За дверью стоит человек, чьё имя вычеркнули из всех родословных. Он не требует короны. Он показывает тебе вторую половину летописи: Альдрик III знал о заговоре и спрятал наследника, чтобы сохранить его жизнь. Теперь тайна раскрыта, но королевство ещё должно решить, готово ли оно услышать правду. Гаррет берёт архив под охрану, а Селеста обещает собрать свидетелей. Ты выходишь из подземелья не победителем, а хранителем истины, от которой зависит будущее Эстервала.", emptyList()),
    Scene("ЭПИЛОГ · СОЮЗНИКИ", "Корона, которую отложили", "Зал великого совета", "Ты не открываешь дверь. Вместо этого семь копий доказательств отправляются к семи домам, а Гаррет получает приказ охранять вход до общего решения. Впервые за много лет дома вынуждены говорить друг с другом, не зная, кто владеет всей правдой. Корона остаётся без нового правителя, но город избегает немедленной войны. Это не окончательная победа — это хрупкий мир, который тебе предстоит защищать.", emptyList()),
    Scene("ЭПИЛОГ · ЧЁРНАЯ РОЗА", "Цена доверия", "Подземный архив Эстервала", "Селеста исчезает за каменной дверью, и проход запечатывается прежде, чем ты успеваешь последовать за ней. На полу остаётся кольцо с чёрной розой и короткая записка: «Не ищи меня, пока не узнаешь, кто написал первое имя». Ты уносишь кольцо наверх. Заговор лишился одного из своих ключей, но его сердце всё ещё бьётся где-то под Эстервальским замком. Ты сохраняешь доказательства и клянешься вернуться, когда соберёшь достаточно сил.", emptyList())
)

@Composable
private fun AshesGame(activity: ComponentActivity) {
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val screenHeightDp = configuration.screenHeightDp
    val ultraCompact = screenWidthDp < 640 || screenHeightDp < 360
    val compactLandscape = screenWidthDp < 820 || screenHeightDp < 430
    val outerPadding = if (ultraCompact) 4.dp else if (compactLandscape) 6.dp else 10.dp
    val columnGap = if (ultraCompact) 4.dp else if (compactLandscape) 6.dp else 9.dp
    val brandWidth = if (ultraCompact) 88.dp else if (compactLandscape) 126.dp else 184.dp
    val leftRailWidth = if (ultraCompact) 42.dp else if (compactLandscape) 54.dp else 72.dp
    val headerHeight = if (ultraCompact) 34.dp else if (compactLandscape) 42.dp else 50.dp
    val prefs = remember { activity.getSharedPreferences("ashes_save_v2", 0) }
    // Normalize all daily markers against one trusted, bounded campaign day.
    val savedWorldDay = prefs.getInt("world_day", 1).coerceIn(1, 1_000_000)
    var musicEnabled by remember { mutableStateOf(prefs.getBoolean("music_enabled", true)) }
    var sceneIndex by remember { mutableIntStateOf(prefs.getInt("scene", 0).coerceIn(scenes.indices)) }
    var reputation by remember { mutableIntStateOf(SaveManager.normalizeInteger("rep", prefs.getInt("rep", 0), scenes.lastIndex)) }
    var guard by remember { mutableIntStateOf(SaveManager.normalizeInteger("guard", prefs.getInt("guard", 20), scenes.lastIndex)) }
    var seleste by remember { mutableIntStateOf(SaveManager.normalizeInteger("seleste", prefs.getInt("seleste", 0), scenes.lastIndex)) }
    var gold by remember { mutableIntStateOf(SaveManager.normalizeInteger("gold", prefs.getInt("gold", 18), scenes.lastIndex)) }
    var health by remember { mutableIntStateOf(SaveManager.normalizeInteger("hp", prefs.getInt("hp", 100), scenes.lastIndex)) }
    var influence by remember { mutableIntStateOf(SaveManager.normalizeInteger("inf", prefs.getInt("inf", 0), scenes.lastIndex)) }
    var crownRep by remember { mutableIntStateOf(SaveManager.normalizeInteger("f_crown", prefs.getInt("f_crown", 0), scenes.lastIndex)) }
    var merchantRep by remember { mutableIntStateOf(SaveManager.normalizeInteger("f_merchants", prefs.getInt("f_merchants", 0), scenes.lastIndex)) }
    var peopleRep by remember { mutableIntStateOf(SaveManager.normalizeInteger("f_people", prefs.getInt("f_people", 0), scenes.lastIndex)) }
    var roseRep by remember { mutableIntStateOf(SaveManager.normalizeInteger("f_rose", prefs.getInt("f_rose", 0), scenes.lastIndex)) }
    var questStage by remember { mutableIntStateOf(SaveManager.normalizeInteger("quest_market", prefs.getInt("quest_market", 0), scenes.lastIndex)) }
    var questMedicineCrateFound by remember { mutableStateOf(prefs.getBoolean("quest_medicine_crate_found", false)) }
    var roseQuestStage by remember { mutableIntStateOf(prefs.getInt("quest_rose_chain", 0).coerceIn(0, 5)) }
    var roseQuestPath by remember { mutableIntStateOf(SaveManager.normalizeInteger("quest_rose_path", prefs.getInt("quest_rose_path", 0), scenes.lastIndex)) }
    var housesQuestStage by remember { mutableIntStateOf(prefs.getInt("quest_houses_stage", 0).coerceIn(0, 3)) }
    var housesQuestPath by remember { mutableIntStateOf(prefs.getInt("quest_houses_path", 0).coerceIn(0, 3)) }
    var garrettMemory by remember { mutableIntStateOf(SaveManager.normalizeInteger("mem_garrett", prefs.getInt("mem_garrett", 0), scenes.lastIndex)) }
    var selesteMemory by remember { mutableIntStateOf(SaveManager.normalizeInteger("mem_seleste", prefs.getInt("mem_seleste", 0), scenes.lastIndex)) }
    var randomEventCount by remember { mutableIntStateOf(SaveManager.normalizeInteger("events", prefs.getInt("events", 0), scenes.lastIndex)) }
    var thomasMemory by remember { mutableIntStateOf(SaveManager.normalizeInteger("mem_thomas", prefs.getInt("mem_thomas", 0), scenes.lastIndex)) }
    var miraMemory by remember { mutableIntStateOf(SaveManager.normalizeInteger("mem_mira", prefs.getInt("mem_mira", 0), scenes.lastIndex)) }
    var thomasBond by remember { mutableIntStateOf(prefs.getInt("bond_thomas", 8).coerceIn(-100,100)) }
    var miraBond by remember { mutableIntStateOf(prefs.getInt("bond_mira", 12).coerceIn(-100,100)) }
    var reynarBond by remember { mutableIntStateOf(prefs.getInt("bond_reynar", -5).coerceIn(-100,100)) }
    var socialConflict by remember { mutableIntStateOf(prefs.getInt("social_conflict", 0).coerceIn(0,3)) }
    var socialEvent by remember { mutableStateOf(prefs.getString("social_event", "В Эстервaле пока царит хрупкое перемирие между знакомыми тебе людьми.") ?: "В Эстервaле пока царит хрупкое перемирие между знакомыми тебе людьми.") }
    var personalQuestFlags by remember { mutableIntStateOf(SaveManager.normalizeInteger("personal_quests", prefs.getInt("personal_quests", 0), scenes.lastIndex)) }
    // Separate one-off relationship scenes from the four personal quest flags.
    // This prevents Celeste/Reynar social scenes from consuming Garrett/Celeste quest bits.
    var socialEventFlags by remember { mutableIntStateOf(SaveManager.normalizeInteger("social_event_flags", prefs.getInt("social_event_flags", 0), scenes.lastIndex)) }
    var socialActionFlags by remember { mutableIntStateOf(SaveManager.normalizeInteger("social_action_flags", prefs.getInt("social_action_flags", 0), scenes.lastIndex)) }
    var dungeonKeep by remember { mutableIntStateOf(prefs.getInt("dungeon_keep", 0).coerceIn(0,4)) }
    var dungeonCrypt by remember { mutableIntStateOf(prefs.getInt("dungeon_crypt", 0).coerceIn(0,4)) }
    var dungeonArchive by remember { mutableIntStateOf(prefs.getInt("dungeon_archive", 0).coerceIn(0,4)) }
    var expeditionNote by remember { mutableStateOf(prefs.getString("expedition_note", "Три забытых места ждут исследователя.") ?: "Три забытых места ждут исследователя.") }
    var weatherSeed by remember { mutableIntStateOf(prefs.getInt("weather_seed", 0)) }
    var craftedCount by remember { mutableIntStateOf(SaveManager.normalizeInteger("crafted_count", prefs.getInt("crafted_count", 0), scenes.lastIndex)) }
    var councilProgress by remember { mutableIntStateOf(prefs.getInt("council_progress", 0).coerceIn(0, 3)) }
    var bossDefeated by remember { mutableStateOf(prefs.getBoolean("boss_defeated", false)) }
    var worldInvestigationFlags by remember { mutableIntStateOf(prefs.getInt("world_investigation_flags", 0) and 0b1111) }
    var councilBalance by remember { mutableIntStateOf(prefs.getInt("council_balance", 0).coerceIn(-20, 20)) }
    var councilReport by remember { mutableStateOf(prefs.getString("council_report", "Совет семи домов ждёт твоего первого выступления.") ?: "Совет семи домов ждёт твоего первого выступления.") }
    var companion by remember { mutableStateOf(CompanionManager.normalizeCompanion(prefs.getString("companion", "Гаррет Морн") ?: "Гаррет Морн")) }
    val legacyCompanionBond = prefs.getInt("companion_bond", 0).coerceIn(-100, 100)
    var companionBondGarrett by remember { mutableIntStateOf(prefs.getInt("bond_companion_garrett", if (companion == "Гаррет Морн") legacyCompanionBond else 0).coerceIn(-100, 100)) }
    var companionBondCeleste by remember { mutableIntStateOf(prefs.getInt("bond_companion_celeste", if (companion == "Селеста") legacyCompanionBond else 0).coerceIn(-100, 100)) }
    var companionBondMira by remember { mutableIntStateOf(prefs.getInt("bond_companion_mira", if (companion == "Мира") legacyCompanionBond else 0).coerceIn(-100, 100)) }
    var companionBondThomas by remember { mutableIntStateOf(prefs.getInt("bond_companion_thomas", if (companion == "Томас") legacyCompanionBond else 0).coerceIn(-100, 100)) }
    var companionTalkDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("companion_talk_day", 0), savedWorldDay)) }
    var companionMomentReport by remember { mutableStateOf(prefs.getString("companion_moment_report", "У каждого спутника есть своя история. Найди время, чтобы поговорить.") ?: "У каждого спутника есть своя история. Найди время, чтобы поговорить.") }
    var storyStageGarrett by remember { mutableIntStateOf(CompanionStoryManager.normalizeStage(prefs.getInt("story_companion_garrett", 0))) }
    var storyStageCeleste by remember { mutableIntStateOf(CompanionStoryManager.normalizeStage(prefs.getInt("story_companion_celeste", 0))) }
    var storyStageMira by remember { mutableIntStateOf(CompanionStoryManager.normalizeStage(prefs.getInt("story_companion_mira", 0))) }
    var storyStageThomas by remember { mutableIntStateOf(CompanionStoryManager.normalizeStage(prefs.getInt("story_companion_thomas", 0))) }
    var companionStoryReport by remember { mutableStateOf(prefs.getString("companion_story_report", "Личные истории спутников ждут своего часа.") ?: "Личные истории спутников ждут своего часа.") }
    var relationGarrettCeleste by remember { mutableIntStateOf(prefs.getInt("relation_garrett_celeste", 0).coerceIn(-100, 100)) }
    var relationGarrettMira by remember { mutableIntStateOf(prefs.getInt("relation_garrett_mira", 0).coerceIn(-100, 100)) }
    var relationGarrettThomas by remember { mutableIntStateOf(prefs.getInt("relation_garrett_thomas", 0).coerceIn(-100, 100)) }
    var relationCelesteMira by remember { mutableIntStateOf(prefs.getInt("relation_celeste_mira", 0).coerceIn(-100, 100)) }
    var relationCelesteThomas by remember { mutableIntStateOf(prefs.getInt("relation_celeste_thomas", 0).coerceIn(-100, 100)) }
    var relationMiraThomas by remember { mutableIntStateOf(prefs.getInt("relation_mira_thomas", 0).coerceIn(-100, 100)) }
    var companionRelationDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("companion_relation_day", 0), savedWorldDay)) }
    var companionRelationReport by remember { mutableStateOf(prefs.getString("companion_relation_report", "Спутники ещё привыкают друг к другу.") ?: "Спутники ещё привыкают друг к другу.") }
    var companionTeamEventFlags by remember { mutableIntStateOf(prefs.getInt("companion_team_event_flags", 0) and CompanionTeamEventManager.ALL_EVENTS_MASK) }
    var companionTeamEventDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("companion_team_event_day", 0), savedWorldDay)) }
    var companionTeamEventReport by remember { mutableStateOf(prefs.getString("companion_team_event_report", "Совместные истории спутников ещё впереди.") ?: "Совместные истории спутников ещё впереди.") }
    var expeditionRuns by remember { mutableIntStateOf(SaveManager.normalizeInteger("expedition_runs", prefs.getInt("expedition_runs", 0), scenes.lastIndex)) }
    var companionExpeditionDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("companion_expedition_day", 0), savedWorldDay)) }
    var expeditionReport by remember { mutableStateOf(prefs.getString("expedition_report", "Выбери спутника и отправляйся в путь.") ?: "Выбери спутника и отправляйся в путь.") }
    var frontierStage by remember { mutableIntStateOf(prefs.getInt("frontier_stage", 0).coerceIn(0, 3)) }
    var frontierRoute by remember { mutableIntStateOf(prefs.getInt("frontier_route", 0).coerceIn(0, 3)) }
    var frontierThreat by remember { mutableIntStateOf(prefs.getInt("frontier_threat", 50).coerceIn(0, 100)) }
    var frontierReport by remember { mutableStateOf(prefs.getString("frontier_report", "Северные земли ждут решения Совета.") ?: "Северные земли ждут решения Совета.") }
    // Alpha 11: persistent realm-management systems; new keys leave legacy saves untouched.
    var royalArmy by remember { mutableIntStateOf(prefs.getInt("a11_army", 24).coerceIn(0, 100)) }
    var provisions by remember { mutableIntStateOf(prefs.getInt("a11_provisions", 30).coerceIn(0, 100)) }
    var treasuryLevel by remember { mutableIntStateOf(prefs.getInt("a11_treasury", 1).coerceIn(1, 3)) }
    var lastTaxDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("a11_tax_day", 0), savedWorldDay)) }
    var realmReport by remember { mutableStateOf(prefs.getString("a11_report", "Королевские владения ждут твоего управления.") ?: "Королевские владения ждут твоего управления.") }
    // Alpha 13: persistent province administration, additive keys preserve existing saves.
    val provinceNames = listOf("Королевские земли", "Северная марка", "Портовые владения", "Золотые поля")
    val provinceDescriptions = listOf("Столица, дворцовые мастерские и главные дороги.", "Пограничные деревни и крепость Вальден.", "Причалы, склады и торговые кварталы.", "Пашни, мельницы и зерновые амбары.")
    // Parse each province slot independently: a malformed middle token must not shift
    // the next province's values into the wrong territory.
    val provinceProsperity = remember { mutableStateListOf<Int>().apply { addAll(SaveDataRules.parseBoundedIntList(prefs.getString("a13_prosperity", null), listOf(62, 38, 55, 68), 0, 100)) } }
    val provinceLoyalty = remember { mutableStateListOf<Int>().apply { addAll(SaveDataRules.parseBoundedIntList(prefs.getString("a13_loyalty", null), listOf(70, 48, 58, 64), 0, 100)) } }
    val provinceStewards = remember { mutableStateListOf<Int>().apply { addAll(SaveDataRules.parseBoundedIntList(prefs.getString("a13_stewards", null), listOf(1, 0, 0, 0), 0, 1)) } }
    val provinceTaxDays = remember { mutableStateListOf<Int>().apply { addAll(SaveDataRules.parseBoundedIntList(prefs.getString("a13_tax_days", null), listOf(-1, -1, -1, -1), -1, savedWorldDay)) } }
    // Consecutive low-loyalty days: 0 = stable, 1-2 = unrest, 3+ = open revolt.
    val provinceCrisisDays = remember { mutableStateListOf<Int>().apply { addAll(SaveDataRules.parseBoundedIntList(prefs.getString("a15_crisis_days", null), listOf(0, 0, 0, 0), 0, 3)) } }
    var provinceReport by remember { mutableStateOf(prefs.getString("a13_report", "Провинции ждут распоряжений короны.") ?: "Провинции ждут распоряжений короны.") }
    var provinceIncomeDay by remember { mutableIntStateOf(SaveDataRules.normalizeDayMarker(prefs.getInt("a13_income_day", 0), savedWorldDay)) }
    var selectedPage by remember { mutableStateOf("story") }
    var slotRefresh by remember { mutableIntStateOf(0) }
    val saveLifecycleGate = remember { SaveLifecycleGate() }
    var confirmNewGame by remember { mutableStateOf(false) }
    var menuVisible by remember { mutableStateOf(true) }
    var dialogNpc by remember { mutableStateOf(prefs.getString("dialog_npc", "Гаррет Морн") ?: "Гаррет Морн") }
    var dialogStage by remember { mutableIntStateOf(prefs.getInt("dialog_stage", 0).coerceIn(0, 3)) }
    var dialogTrust by remember { mutableIntStateOf(prefs.getInt("dialog_trust", 0).coerceIn(-20, 20)) }
    val restoredBattle = BattleManager.restoreEncounter(
        prefs.getString("battle_enemy", ""), prefs.getInt("battle_enemy_hp", 0)
    )
    var enemyName by remember { mutableStateOf(restoredBattle.enemyName) }
    var enemyHealth by remember { mutableIntStateOf(restoredBattle.enemyHealth) }
    var battleWins by remember { mutableIntStateOf(SaveManager.normalizeInteger("battle_wins", prefs.getInt("battle_wins", 0), scenes.lastIndex)) }
    var battleDefeats by remember { mutableIntStateOf(SaveManager.normalizeInteger("battle_defeats", prefs.getInt("battle_defeats", 0), scenes.lastIndex)) }
    // Ordinary combat rewards are limited per in-game day to discourage encounter farming.
    var battleRewardDay by remember { mutableIntStateOf(prefs.getInt("battle_reward_day", savedWorldDay).coerceIn(1, savedWorldDay)) }
    var battleRewardsToday by remember { mutableIntStateOf(prefs.getInt("battle_rewards_today", 0).coerceIn(0, 4)) }
    // A defeat causes a temporary wound until the next in-game day.
    var woundedUntilDay by remember { mutableIntStateOf(prefs.getInt("wounded_until_day", 0).coerceAtLeast(0)) }
    var experience by remember { mutableIntStateOf(SaveManager.normalizeInteger("experience", prefs.getInt("experience", 0), scenes.lastIndex)) }
    var heroLevel by remember { mutableIntStateOf(SaveManager.normalizeInteger("hero_level", prefs.getInt("hero_level", 1), scenes.lastIndex)) }
    var skillPoints by remember { mutableIntStateOf(SaveManager.normalizeInteger("skill_points", prefs.getInt("skill_points", 0), scenes.lastIndex)) }
    var powerSkill by remember { mutableIntStateOf(SaveManager.normalizeInteger("skill_power", prefs.getInt("skill_power", 0), scenes.lastIndex)) }
    var guardSkill by remember { mutableIntStateOf(SaveManager.normalizeInteger("skill_guard", prefs.getInt("skill_guard", 0), scenes.lastIndex)) }
    var battlePulse by remember { mutableIntStateOf(0) }
    var encounterCount by remember { mutableIntStateOf(SaveManager.normalizeInteger("encounters", prefs.getInt("encounters", 0), scenes.lastIndex)) }
    val enemyHitScale by animateFloatAsState(targetValue = if (battlePulse % 2 == 0) 1f else 0.94f, label = "enemyHit")
    var battleLog by remember { mutableStateOf(prefs.getString("battle_log", "Здесь появится ход сражения.") ?: "Здесь появится ход сражения.") }
    var battleGuarding by remember { mutableStateOf(false) }
    // Never let a malformed or obsolete location string soft-lock the travel system.
    var currentLocation by remember { mutableStateOf(WorldManager.normalizeLocation(prefs.getString("location", "Королевский замок"))) }
    var worldDay by remember { mutableIntStateOf(savedWorldDay) }
    var worldHour by remember { mutableIntStateOf(prefs.getInt("world_hour", 9).coerceIn(0, 23)) }
    // Tracks the last in-game day for which the royal army's provisions were charged.
    // Legacy saves start at their current day, so migration never applies retroactive upkeep.
    var lastUpkeepDay by remember { mutableIntStateOf(prefs.getInt("a14_upkeep_day", savedWorldDay).coerceIn(1, savedWorldDay)) }
    var worldEvent by remember { mutableStateOf(prefs.getString("world_event", "Город просыпается под звон колоколов.") ?: "Город просыпается под звон колоколов.") }
    // Original offline soundtrack: six bundled themes, selected by location and world event.
    val musicTheme = when {
        worldEvent.contains("бой", ignoreCase = true) || worldEvent.contains("сраж", ignoreCase = true) || worldEvent.contains("ата", ignoreCase = true) -> "music_battle"
        worldEvent.contains("тайн", ignoreCase = true) || worldEvent.contains("ули", ignoreCase = true) || currentLocation.contains("часов", ignoreCase = true) || currentLocation.contains("мост", ignoreCase = true) -> "music_mystery"
        currentLocation.contains("город", ignoreCase = true) || currentLocation.contains("рын", ignoreCase = true) || currentLocation.contains("деревн", ignoreCase = true) -> "music_city"
        currentLocation.contains("лес", ignoreCase = true) || currentLocation.contains("сад", ignoreCase = true) || currentLocation.contains("тракт", ignoreCase = true) -> "music_nature"
        currentLocation.contains("порт", ignoreCase = true) -> "music_harbor"
        else -> "music_court"
    }
    var musicPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    val latestMusicPlayer by rememberUpdatedState(musicPlayer)
    LaunchedEffect(musicEnabled, musicTheme) {
        musicPlayer?.release()
        musicPlayer = null
        if (musicEnabled) {
            val themedId = activity.resources.getIdentifier(musicTheme, "raw", activity.packageName)
            val selectedId = if (themedId != 0) themedId else R.raw.estervale_ambient
            musicPlayer = MediaPlayer.create(activity, selectedId)?.apply {
                isLooping = true
                setVolume(0.34f, 0.34f)
                start()
            }
        }
    }
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_START -> latestMusicPlayer?.let { if (!it.isPlaying) it.start() }
                Lifecycle.Event.ON_STOP -> latestMusicPlayer?.let { if (it.isPlaying) it.pause() }
                else -> Unit
            }
        }
        activity.lifecycle.addObserver(observer)
        onDispose {
            activity.lifecycle.removeObserver(observer)
            latestMusicPlayer?.release()
        }
    }
    var lastOutcome by remember { mutableStateOf(SaveDataRules.decodeOptionalText(prefs.getString("outcome", null))) }
    var chosen by remember { mutableStateOf(prefs.getBoolean("chosen", false)) }
    val journal = remember { mutableStateListOf<String>().apply { addAll(JournalManager.normalize(prefs.getString("journal", "").orEmpty().split("¦"))) } }
    fun addJournalEntry(entry: String) {
        val updated = JournalManager.append(journal.toList(), entry)
        if (updated != journal.toList()) { journal.clear(); journal.addAll(updated) }
    }
    val inventory = remember { mutableStateListOf<String>().apply { addAll(InventoryManager.normalize(prefs.getString("items", "Архивный ключ¦Дорожный плащ¦Пустой блокнот").orEmpty().split("¦"))) } }
    fun addInventoryItem(item: String): Boolean {
        val change = InventoryManager.add(inventory.toList(), item)
        if (change.changed) { inventory.clear(); inventory.addAll(change.items) }
        else if (change.message.isNotBlank()) lastOutcome = change.message
        return change.changed
    }
    fun removeInventoryItem(item: String): Boolean {
        val change = InventoryManager.removeOne(inventory.toList(), item)
        if (change.changed) { inventory.clear(); inventory.addAll(change.items) }
        return change.changed
    }
    fun removeAllInventoryItems(item: String): Boolean {
        val change = InventoryManager.removeAll(inventory.toList(), item)
        if (change.changed) { inventory.clear(); inventory.addAll(change.items) }
        return change.changed
    }
    fun hasInventoryItem(item: String): Boolean = InventoryManager.contains(inventory, item)
    fun inventoryItemCount(item: String): Int = InventoryManager.count(inventory, item)
    fun selectedCompanionBond(): Int = when (companion) {
        "Гаррет Морн" -> companionBondGarrett
        "Селеста" -> companionBondCeleste
        "Мира" -> companionBondMira
        else -> companionBondThomas
    }
    fun setCompanionBond(name: String, value: Int) {
        val normalized = CompanionManager.normalizeBond(value)
        when (CompanionManager.normalizeCompanion(name)) {
            "Гаррет Морн" -> companionBondGarrett = normalized
            "Селеста" -> companionBondCeleste = normalized
            "Мира" -> companionBondMira = normalized
            else -> companionBondThomas = normalized
        }
    }
    fun companionBondFor(name: String): Int = when (CompanionManager.normalizeCompanion(name)) {
        "Гаррет Морн" -> companionBondGarrett
        "Селеста" -> companionBondCeleste
        "Мира" -> companionBondMira
        else -> companionBondThomas
    }
    fun selectedCompanionStoryStage(): Int = when (companion) {
        "Гаррет Морн" -> storyStageGarrett
        "Селеста" -> storyStageCeleste
        "Мира" -> storyStageMira
        else -> storyStageThomas
    }
    fun setCompanionStoryStage(name: String, value: Int) {
        val stage = CompanionStoryManager.normalizeStage(value)
        when (CompanionManager.normalizeCompanion(name)) {
            "Гаррет Морн" -> storyStageGarrett = stage
            "Селеста" -> storyStageCeleste = stage
            "Мира" -> storyStageMira = stage
            else -> storyStageThomas = stage
        }
    }
    fun companionPairScore(pair: CompanionPair): Pair<Int, Int> = when (pair) {
        CompanionPair.GARRETT_CELESTE -> relationGarrettCeleste to relationGarrettCeleste
        CompanionPair.GARRETT_MIRA -> relationGarrettMira to relationGarrettMira
        CompanionPair.GARRETT_THOMAS -> relationGarrettThomas to relationGarrettThomas
        CompanionPair.CELESTE_MIRA -> relationCelesteMira to relationCelesteMira
        CompanionPair.CELESTE_THOMAS -> relationCelesteThomas to relationCelesteThomas
        CompanionPair.MIRA_THOMAS -> relationMiraThomas to relationMiraThomas
    }
    fun setCompanionPairScore(pair: CompanionPair, first: Int, second: Int) {
        // A pair has one symmetric affinity score: both members experience the same relationship.
        val score = CompanionRelationshipManager.normalize((first + second) / 2)
        when (pair) {
            CompanionPair.GARRETT_CELESTE -> relationGarrettCeleste = score
            CompanionPair.GARRETT_MIRA -> relationGarrettMira = score
            CompanionPair.GARRETT_THOMAS -> relationGarrettThomas = score
            CompanionPair.CELESTE_MIRA -> relationCelesteMira = score
            CompanionPair.CELESTE_THOMAS -> relationCelesteThomas = score
            CompanionPair.MIRA_THOMAS -> relationMiraThomas = score
        }
    }
    fun save() {
        prefs.edit().putInt("save_schema_version", SaveManager.CURRENT_SCHEMA)
            .putInt("scene", sceneIndex).putInt("rep", reputation).putInt("guard", guard)
            .putInt("seleste", seleste).putInt("gold", gold).putInt("hp", health).putInt("inf", influence)
            .putInt("f_crown", crownRep).putInt("f_merchants", merchantRep).putInt("f_people", peopleRep).putInt("f_rose", roseRep)
            .putInt("quest_market", questStage).putBoolean("quest_medicine_crate_found", questMedicineCrateFound).putInt("quest_rose_chain", roseQuestStage).putInt("quest_rose_path", roseQuestPath).putInt("quest_houses_stage", housesQuestStage).putInt("quest_houses_path", housesQuestPath).putInt("mem_garrett", garrettMemory).putInt("mem_seleste", selesteMemory).putInt("events", randomEventCount)
            .putInt("mem_thomas", thomasMemory).putInt("mem_mira", miraMemory).putInt("bond_thomas", thomasBond).putInt("bond_mira", miraBond).putInt("bond_reynar", reynarBond).putInt("social_conflict", socialConflict).putString("social_event", socialEvent).putInt("personal_quests", personalQuestFlags).putInt("social_event_flags", socialEventFlags).putInt("social_action_flags", socialActionFlags)
            .putString("location", currentLocation).putInt("world_day", worldDay).putInt("world_hour", worldHour).putString("world_event", worldEvent)
            .putString("battle_enemy", enemyName).putInt("battle_enemy_hp", enemyHealth)
            .putInt("battle_wins", battleWins).putInt("battle_defeats", battleDefeats)
            .putInt("battle_reward_day", battleRewardDay).putInt("battle_rewards_today", battleRewardsToday)
            .putInt("wounded_until_day", woundedUntilDay).putString("battle_log", battleLog)
            .putInt("experience", experience).putInt("hero_level", heroLevel).putInt("skill_points", skillPoints)
            .putInt("skill_power", powerSkill).putInt("skill_guard", guardSkill).putInt("encounters", encounterCount)
            .putString("outcome", SaveDataRules.encodeOptionalText(lastOutcome)).putBoolean("chosen", chosen)
            .putString("dialog_npc", dialogNpc).putInt("dialog_stage", dialogStage).putInt("dialog_trust", dialogTrust)
            .putInt("dialog_stage_$dialogNpc", dialogStage).putInt("dialog_trust_$dialogNpc", dialogTrust)
            .putString("journal", journal.joinToString("¦")).putString("items", inventory.joinToString("¦"))
            .putInt("dungeon_keep", dungeonKeep).putInt("dungeon_crypt", dungeonCrypt).putInt("dungeon_archive", dungeonArchive)
            .putString("expedition_note", expeditionNote).putInt("weather_seed", weatherSeed).putInt("crafted_count", craftedCount)
            .putInt("council_progress", councilProgress).putInt("council_balance", councilBalance).putString("council_report", councilReport)
            .putBoolean("boss_defeated", bossDefeated).putInt("world_investigation_flags", worldInvestigationFlags)
            .putString("companion", companion).putInt("expedition_runs", expeditionRuns).putInt("companion_expedition_day", companionExpeditionDay)
            .putInt("companion_bond", selectedCompanionBond()).putInt("bond_companion_garrett", companionBondGarrett)
            .putInt("bond_companion_celeste", companionBondCeleste).putInt("bond_companion_mira", companionBondMira)
            .putInt("bond_companion_thomas", companionBondThomas).putInt("companion_talk_day", companionTalkDay)
            .putString("companion_moment_report", companionMomentReport)
            .putInt("story_companion_garrett", storyStageGarrett).putInt("story_companion_celeste", storyStageCeleste)
            .putInt("story_companion_mira", storyStageMira).putInt("story_companion_thomas", storyStageThomas)
            .putString("companion_story_report", companionStoryReport)
            .putInt("relation_garrett_celeste", relationGarrettCeleste).putInt("relation_garrett_mira", relationGarrettMira)
            .putInt("relation_garrett_thomas", relationGarrettThomas).putInt("relation_celeste_mira", relationCelesteMira)
            .putInt("relation_celeste_thomas", relationCelesteThomas).putInt("relation_mira_thomas", relationMiraThomas)
            .putInt("companion_relation_day", companionRelationDay).putString("companion_relation_report", companionRelationReport)
            .putInt("companion_team_event_flags", companionTeamEventFlags).putInt("companion_team_event_day", companionTeamEventDay)
            .putString("companion_team_event_report", companionTeamEventReport)
            .putString("expedition_report", expeditionReport)
            .putInt("frontier_stage", frontierStage).putInt("frontier_route", frontierRoute).putInt("frontier_threat", frontierThreat).putString("frontier_report", frontierReport)
            .putInt("a11_army", royalArmy).putInt("a11_provisions", provisions).putInt("a11_treasury", treasuryLevel).putInt("a11_tax_day", lastTaxDay).putInt("a14_upkeep_day", lastUpkeepDay).putString("a11_report", realmReport)
            .putString("a13_prosperity", provinceProsperity.joinToString(",")).putString("a13_loyalty", provinceLoyalty.joinToString(",")).putString("a13_stewards", provinceStewards.joinToString(",")).putString("a13_tax_days", provinceTaxDays.joinToString(",")).putString("a15_crisis_days", provinceCrisisDays.joinToString(",")).putString("a13_report", provinceReport).putInt("a13_income_day", provinceIncomeDay).apply()
    }
    val saveSlotStore = remember { SaveSlotStore(prefs, scenes.lastIndex) }
    fun saveSlot(slot: Int) {
        if (slot !in 1..3) return
        save()
        val label = "День $worldDay · ${worldHour.toString().padStart(2, '0')}:00 · $currentLocation"
        when (val result = saveSlotStore.saveSlot(slot, label)) {
            is SaveSlotResult.Saved -> android.widget.Toast.makeText(
                activity, "Сохранение записано в слот ${result.slot}.", android.widget.Toast.LENGTH_SHORT
            ).show()
            is SaveSlotResult.Rejected -> android.widget.Toast.makeText(
                activity, result.message, android.widget.Toast.LENGTH_LONG
            ).show()
            else -> Unit
        }
    }
    fun loadSlot(slot: Int) {
        when (val result = saveSlotStore.loadSlot(slot)) {
            is SaveSlotResult.Loaded -> {
                // The outgoing Activity still owns stale Compose state. Do not let its
                // ON_STOP safety save overwrite the slot that was just applied.
                saveLifecycleGate.suppressNextStopSave()
                activity.recreate()
            }
            is SaveSlotResult.Rejected -> android.widget.Toast.makeText(
                activity, result.message, android.widget.Toast.LENGTH_LONG
            ).show()
            SaveSlotResult.Missing -> android.widget.Toast.makeText(
                activity, "В этом слоте пока нет сохранения.", android.widget.Toast.LENGTH_SHORT
            ).show()
            is SaveSlotResult.Saved -> Unit
        }
    }
    val latestLifecycleSave by rememberUpdatedState {
        if (saveLifecycleGate.shouldSaveOnStop()) save()
    }
    DisposableEffect(activity) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) latestLifecycleSave()
        }
        activity.lifecycle.addObserver(observer)
        onDispose { activity.lifecycle.removeObserver(observer) }
    }
    fun grantExperience(amount: Int) {
        val result = PlayerProgressManager.grantExperience(
            PlayerProgressState(heroLevel, experience, skillPoints), amount
        )
        heroLevel = result.state.level
        experience = result.state.experience
        skillPoints = result.state.skillPoints
        result.message?.let { message -> lastOutcome = message; addJournalEntry(message) }
    }
    // Unique story evidence and quest artefacts must never be duplicated by a
    // repeated callback, a restored UI state, or a future quest branch.
    fun addUniqueQuestItem(item: String): Boolean = addInventoryItem(item)
    fun applyMedicineQuestTransition(transition: MedicineQuestTransition) {
        if (!transition.changed) {
            lastOutcome = transition.outcome
            save()
            return
        }
        val inventoryDelta = InventoryManager.applyQuestDelta(
            inventory.toList(), transition.removeItems, transition.addItems
        )
        if (inventoryDelta.message.isNotBlank()) {
            lastOutcome = inventoryDelta.message
            save()
            return
        }
        inventory.clear()
        inventory.addAll(inventoryDelta.items)
        questStage = transition.state.stage
        questMedicineCrateFound = transition.state.crateFound
        gold = (gold + transition.goldReward).coerceIn(0, 1_000_000)
        peopleRep = (peopleRep + transition.commonerReputationReward).coerceIn(-100, 100)
        transition.journalEntry?.let { addJournalEntry(it) }
        lastOutcome = transition.outcome
        save()
    }
    fun resetNewGame() {
        sceneIndex = 0
        reputation = 0; guard = 20; seleste = 0; gold = 18; health = 100; influence = 0
        crownRep = 0; merchantRep = 0; peopleRep = 0; roseRep = 0
        questStage = 0; questMedicineCrateFound = false; roseQuestStage = 0; roseQuestPath = 0; housesQuestStage = 0; housesQuestPath = 0
        garrettMemory = 0; selesteMemory = 0; thomasMemory = 0; miraMemory = 0; randomEventCount = 0
        thomasBond = 8; miraBond = 12; reynarBond = -5; socialConflict = 0
        socialEvent = "В Эстервaле пока царит хрупкое перемирие между знакомыми тебе людьми."
        personalQuestFlags = 0; socialEventFlags = 0; socialActionFlags = 0
        dungeonKeep = 0; dungeonCrypt = 0; dungeonArchive = 0
        expeditionNote = "Три забытых места ждут исследователя."; weatherSeed = 0; craftedCount = 0
        councilProgress = 0; councilBalance = 0; councilReport = "Совет семи домов ждёт твоего первого выступления."
        bossDefeated = false; worldInvestigationFlags = 0
        companion = "Гаррет Морн"; expeditionRuns = 0; companionExpeditionDay = 0
        companionBondGarrett = 0; companionBondCeleste = 0; companionBondMira = 0; companionBondThomas = 0
        companionTalkDay = 0; companionMomentReport = "У каждого спутника есть своя история. Найди время, чтобы поговорить."
        storyStageGarrett = 0; storyStageCeleste = 0; storyStageMira = 0; storyStageThomas = 0
        companionStoryReport = "Личные истории спутников ждут своего часа."
        relationGarrettCeleste = 0; relationGarrettMira = 0; relationGarrettThomas = 0
        relationCelesteMira = 0; relationCelesteThomas = 0; relationMiraThomas = 0
        companionRelationDay = 0; companionRelationReport = "Спутники ещё привыкают друг к другу."
        companionTeamEventFlags = 0; companionTeamEventDay = 0
        companionTeamEventReport = "Совместные истории спутников ещё впереди."
        expeditionReport = "Выбери спутника и отправляйся в путь."
        frontierStage = 0; frontierRoute = 0; frontierThreat = 50
        frontierReport = "Северные земли ждут решения Совета."
        royalArmy = 24; provisions = 30; treasuryLevel = 1; lastTaxDay = 0; lastUpkeepDay = 1
        realmReport = "Королевские владения ждут твоего управления."
        provinceProsperity.clear(); provinceProsperity.addAll(listOf(62, 38, 55, 68))
        provinceLoyalty.clear(); provinceLoyalty.addAll(listOf(70, 48, 58, 64))
        provinceStewards.clear(); provinceStewards.addAll(listOf(1, 0, 0, 0))
        provinceTaxDays.clear(); provinceTaxDays.addAll(listOf(-1, -1, -1, -1))
        provinceCrisisDays.clear(); provinceCrisisDays.addAll(listOf(0, 0, 0, 0))
        provinceReport = "Провинции ждут распоряжений короны."; provinceIncomeDay = 0
        dialogNpc = "Гаррет Морн"; dialogStage = 0; dialogTrust = 0
        listOf("Гаррет Морн", "Селеста", "Мира", "Томас", "Герцог Рейнар").forEach { npc ->
            prefs.edit().remove("dialog_stage_$npc").remove("dialog_trust_$npc").apply()
        }
        enemyName = ""; enemyHealth = 0; battleWins = 0; battleDefeats = 0
        battleRewardDay = 1; battleRewardsToday = 0; woundedUntilDay = 0
        experience = 0; heroLevel = 1
        skillPoints = 0; powerSkill = 0; guardSkill = 0; battlePulse = 0; encounterCount = 0
        battleLog = "Здесь появится ход сражения."; battleGuarding = false
        currentLocation = "Королевский замок"; worldDay = 1; worldHour = 9
        worldEvent = "Город просыпается под звон колоколов."
        lastOutcome = null; chosen = false
        journal.clear()
        inventory.clear(); inventory.addAll(listOf("Архивный ключ", "Дорожный плащ", "Пустой блокнот"))
        selectedPage = "story"
        confirmNewGame = false
        save()
    }
    fun makeChoice(choice: GameChoice) {
        // Pure choice resolution lives outside the Compose screen. The UI only
        // applies the validated result and performs presentation/persistence work.
        val resolution = GameActionRules.resolveChoiceWithInventory(
            state = GameChoiceState(
                reputation = reputation, guard = guard, seleste = seleste, gold = gold,
                health = health, influence = influence, crownRep = crownRep,
                merchantRep = merchantRep, peopleRep = peopleRep, roseRep = roseRep
            ),
            choice = choice,
            inventory = inventory.toList()
        )
        if (!resolution.accepted) {
            lastOutcome = resolution.rejectionMessage
            save()
            return
        }
        val updated = resolution.state
        reputation = updated.reputation; guard = updated.guard; seleste = updated.seleste
        gold = updated.gold; health = updated.health; influence = updated.influence
        crownRep = updated.crownRep; merchantRep = updated.merchantRep
        peopleRep = updated.peopleRep; roseRep = updated.roseRep
        inventory.clear(); inventory.addAll(resolution.inventory)
        lastOutcome = choice.outcome
        addJournalEntry("${scenes[sceneIndex].title}: ${choice.title}")
        sceneIndex = choice.nextScene.coerceIn(scenes.indices)
        chosen = true; selectedPage = "story"; save()
    }
    fun beginBattle(name: String, hp: Int) {
        if (enemyHealth > 0) {
            battleLog = "Сражение уже идёт против $enemyName. Сначала заверши текущий бой."
            selectedPage = "battle"
            return
        }
        if (name.contains("Древний рыцарь") && bossDefeated) {
            battleLog = "Древний рыцарь уже повержен. Его уникальная награда получена."
            return
        }
        enemyName = name
        enemyHealth = hp.coerceAtLeast(1)
        battleLog = "$name выходит из тумана. Он замечает тебя и поднимает оружие."
        worldEvent = "Началось сражение с противником: $name."
        battleGuarding = false
        selectedPage = "battle"
        save()
    }
    fun enemyTurn(counterDamageBonus: Int = 0) {
        if (enemyHealth <= 0) return
        // Heavy attacks trade higher damage for a stronger enemy counterattack.
        val wounded = worldDay < woundedUntilDay
        val woundDamageBonus = BattleRules.woundIncomingDamageBonus(wounded)
        val rawDamage = (7..13).random() + (if (enemyName.contains("Древний рыцарь")) 4 else 0) + counterDamageBonus + woundDamageBonus
        val armor = BattleRules.armorValue(
            hasReinforcedArmor = EquipmentManager.armorBonus(inventory) == 5,
            hasGuardArmor = EquipmentManager.armorBonus(inventory) == 3,
            guardSkill = guardSkill
        )
        val damage = BattleManager.enemyAttack(rawDamage, armor, battleGuarding)
        health = (health - damage).coerceAtLeast(0)
        battleLog += "\n$enemyName наносит $damage урона${if (battleGuarding) "; стойка помогла смягчить удар" else ""}${if (counterDamageBonus > 0) "; противник воспользовался открытой защитой после тяжёлого замаха" else ""}."
        battleGuarding = false
        if (health == 0) {
            battleDefeats++
            val defeat = BattleManager.defeat(gold, worldDay, woundedUntilDay)
            val goldLoss = defeat.goldLoss
            gold -= goldLoss
            health = defeat.healthAfter
            woundedUntilDay = defeat.woundedUntilDay
            battleLog += "\nТы теряешь сознание. Стражники возвращают тебя в безопасное место. Потеряно $goldLoss золота; здоровье восстановлено до 35. До следующего игрового дня ты ранен: атака слабее, а получаемый урон выше."
            addJournalEntry("Поражение в бою: $enemyName. Потеряно $goldLoss золота; герой ранен до следующего игрового дня.")
            enemyHealth = 0; enemyName = ""
            worldEvent = "После поражения стражники возвращают тебя в безопасное место."
        }
        save()
    }
    fun winBattle() {
        // A delayed/double Compose callback may arrive after the first victory cleared
        // the encounter. Finalize only a still-named enemy at exactly zero HP.
        if (!BattleManager.canResolveVictory(enemyName, enemyHealth)) return
        val defeated = enemyName
        val isAncientKnight = defeated.contains("Древний рыцарь")
        val firstBossVictory = isAncientKnight && !bossDefeated
        if (firstBossVictory && !hasInventoryItem("Реликвия Древнего рыцаря")) {
            val relic = InventoryManager.add(inventory.toList(), "Реликвия Древнего рыцаря")
            if (!relic.changed) {
                enemyName = ""; enemyHealth = 0; battleGuarding = false
                battleLog += "\nПобеда! Но сумка заполнена: освободи место и одолей рыцаря снова, чтобы забрать уникальную реликвию. Награда за босса пока не выдана."
                worldEvent = "Древний рыцарь повержен, но его реликвия осталась на месте битвы."
                addJournalEntry("Древний рыцарь повержен, но уникальная реликвия не получена: инвентарь заполнен.")
                save()
                return
            }
            inventory.clear(); inventory.addAll(relic.items)
        }
        if (firstBossVictory) bossDefeated = true
        if (battleRewardDay != worldDay) {
            battleRewardDay = worldDay
            battleRewardsToday = 0
        }
        // The unique first boss victory is always paid. Ordinary victories grant
        // full rewards for up to four fights per day; later wins remain victories
        // but no longer generate repeatable XP, gold or reputation.
        val rewardPlan = BattleManager.rewards(
            defeatedName = defeated,
            firstBossVictory = firstBossVictory,
            rewardsToday = battleRewardsToday
        )
        val rewardEligible = rewardPlan.eligible
        val rewardXp = rewardPlan.experience
        val rewardGold = rewardPlan.gold
        battleWins++
        if (rewardEligible) {
            gold = ResourceRules.addGold(gold, rewardGold)
            reputation = (reputation + rewardPlan.reputation).coerceAtMost(100)
            if (rewardPlan.countsTowardDailyLimit) battleRewardsToday++
            if (rewardXp > 0) grantExperience(rewardXp)
        }
        if (!isAncientKnight) addUniqueQuestItem("Трофей: знак $defeated")
        val rewardNote = if (rewardEligible) "Получено опыта: $rewardXp, золота: $rewardGold." else "Дневной предел обычных боевых наград исчерпан: опыт, золото и репутация не начислены."
        addJournalEntry("Победа в бою: $defeated. $rewardNote")
        battleLog += "\nПобеда! ${if (rewardEligible) "Получено $rewardXp опыта и $rewardGold золота." else "Дневной предел наград исчерпан; победа записана без повторной награды."}${if (firstBossVictory) " Получена уникальная реликвия." else if (isAncientKnight) " Уникальная награда уже была получена." else ""}"
        enemyHealth = 0
        enemyName = ""
        worldEvent = "Тревога миновала. Вокруг снова слышны звуки королевства."
        save()
    }
    fun startRandomEncounter() {
        if (enemyHealth > 0) {
            battleLog = "Сначала заверши текущее сражение. Новая встреча не началась."
            selectedPage = "battle"
            return
        }
        encounterCount++
        val roll = (1..100).random()
        val (name, hp) = when {
            !bossDefeated && roll <= 8 -> "Древний рыцарь Чёрной розы" to 105
            roll <= 35 -> "Наёмник Чёрной розы" to 48
            roll <= 68 -> "Городской головорез" to 32
            else -> "Дорожный разбойник" to 25
        }
        addJournalEntry("Случайная встреча в пути: $name.")
        beginBattle(name, hp)
        battleLog = "В пути внезапно появляется $name. Бежать или принять бой?"
        save()
    }
    fun advanceWorldTime(hours: Int) {
        val clockAdvance = WorldClockManager.advance(
            currentDay = worldDay,
            currentHour = worldHour,
            requestedHours = hours,
            lastUpkeepDay = lastUpkeepDay
        )
        val newDay = clockAdvance.targetDay
        var suppliedDays = 0
        var shortageDays = 0
        var totalRationsUsed = 0
        var totalSoldiersLost = 0
        var totalProvinceIncome = 0
        var totalStewardWages = 0
        var unpaidStewardDays = 0
        var processedDays = 0
        var crisisSummary = ""
        val worldEventNotes = mutableListOf<String>()
        // Resolve every crossed day with a deterministic rules component. This keeps
        // travel, army upkeep, province crises and steward payroll in one sequence.
        repeat(clockAdvance.pendingUpkeepDays) {
            lastUpkeepDay++
            processedDays++
            val provinceSnapshot = provinceNames.indices.map { i ->
                ProvinceDayState(
                    prosperity = provinceProsperity[i],
                    loyalty = provinceLoyalty[i],
                    steward = provinceStewards[i] == 1,
                    crisisDays = provinceCrisisDays[i]
                )
            }
            val gameStateSnapshot = GameState(
                worldDay = worldDay, worldHour = worldHour, gold = gold, health = health,
                sceneIndex = sceneIndex, inventory = inventory.toList(), journal = journal.toList(),
                realm = GameState.realmSnapshot(royalArmy, provisions, guard, provinceSnapshot)
            )
            val dayResult = RealmDayProcessor.processDay(
                armySize = gameStateSnapshot.realm.armySize,
                provisions = gameStateSnapshot.realm.provisions,
                guard = gameStateSnapshot.realm.guard,
                gold = gameStateSnapshot.gold,
                provinces = gameStateSnapshot.realm.provinces,
                processProvinceEconomy = provinceIncomeDay < lastUpkeepDay
            )
            val updatedRealm = gameStateSnapshot.realm.apply(dayResult)
            royalArmy = updatedRealm.armySize
            provisions = updatedRealm.provisions
            guard = updatedRealm.guard
            gold = dayResult.gold
            updatedRealm.provinces.forEachIndexed { i, province ->
                provinceProsperity[i] = province.prosperity
                provinceLoyalty[i] = province.loyalty
                provinceCrisisDays[i] = province.crisisDays
            }
            // Political and regional consequences fire once when a province crosses
            // a crisis threshold; staying in the same stage does not repeatedly punish it.
            val crisisTransitions = CrisisConsequenceManager.resolve(
                names = provinceNames,
                previousDays = provinceSnapshot.map { it.crisisDays },
                currentDays = provinceCrisisDays.toList()
            )
            crisisTransitions.forEach { transition ->
                crownRep = FactionManager.applyDelta(crownRep, transition.crownDelta)
                merchantRep = FactionManager.applyDelta(merchantRep, transition.merchantDelta)
                peopleRep = FactionManager.applyDelta(peopleRep, transition.peopleDelta)
                roseRep = FactionManager.applyDelta(roseRep, transition.roseDelta)
                if (transition.neighboringLoyaltyLoss > 0) {
                    CrisisConsequenceManager.affectedNeighbors(transition.provinceIndex, provinceLoyalty.size)
                        .forEach { neighbor ->
                            provinceLoyalty[neighbor] = (provinceLoyalty[neighbor] - transition.neighboringLoyaltyLoss).coerceAtLeast(0)
                        }
                }
                worldEventNotes += transition.message
                addJournalEntry("Политические последствия · день $lastUpkeepDay: ${transition.message}")
            }
            val dailyWorldEvent = WorldEventsManager.resolve(
                WorldEventContext(
                    day = lastUpkeepDay,
                    gold = gold,
                    influence = influence,
                    guard = guard,
                    crownReputation = crownRep,
                    merchantReputation = merchantRep,
                    peopleReputation = peopleRep,
                    roseReputation = roseRep,
                    provinceLoyalty = provinceLoyalty.toList(),
                    provinceProsperity = provinceProsperity.toList(),
                    provinceCrisisDays = provinceCrisisDays.toList()
                )
            )
            gold = (gold.toLong() + dailyWorldEvent.goldDelta.toLong()).coerceIn(0L, 1_000_000L).toInt()
            influence = (influence.toLong() + dailyWorldEvent.influenceDelta.toLong()).coerceIn(0L, 100L).toInt()
            guard = (guard.toLong() + dailyWorldEvent.guardDelta.toLong()).coerceIn(0L, 100L).toInt()
            crownRep = FactionManager.applyDelta(crownRep, dailyWorldEvent.crownReputationDelta)
            merchantRep = FactionManager.applyDelta(merchantRep, dailyWorldEvent.merchantReputationDelta)
            peopleRep = FactionManager.applyDelta(peopleRep, dailyWorldEvent.peopleReputationDelta)
            roseRep = FactionManager.applyDelta(roseRep, dailyWorldEvent.roseReputationDelta)
            dailyWorldEvent.provinceIndex?.let { index ->
                if (index in provinceLoyalty.indices) {
                    provinceLoyalty[index] = (provinceLoyalty[index] + dailyWorldEvent.provinceLoyaltyDelta).coerceIn(0, 100)
                    provinceProsperity[index] = (provinceProsperity[index] + dailyWorldEvent.provinceProsperityDelta).coerceIn(0, 100)
                }
            }
            val eventNote = "${dailyWorldEvent.title}: ${dailyWorldEvent.description}"
            worldEventNotes += eventNote
            addJournalEntry("Событие королевства · день $lastUpkeepDay: $eventNote")
            if (dayResult.supplied) suppliedDays++
            if (dayResult.shortage > 0) shortageDays++
            totalRationsUsed += dayResult.rationsUsed
            totalSoldiersLost += dayResult.soldiersLost
            if (dayResult.provinceEconomyProcessed) {
                totalProvinceIncome += dayResult.grossProvinceIncome
                totalStewardWages += dayResult.stewardWagesPaid
                unpaidStewardDays += dayResult.unpaidStewards
                provinceIncomeDay = lastUpkeepDay
                val crisisReport = WorldCrisisManager.evaluate(
                    provinceNames, provinceLoyalty, provinceProsperity, provinceCrisisDays
                )
                crisisSummary = crisisReport.summary
            }
        }
        worldDay = newDay
        if (battleRewardDay != worldDay) {
            battleRewardDay = worldDay
            battleRewardsToday = 0
        }
        if (processedDays > 0) {
            provinceReport = "За $processedDays дн. владения принесли $totalProvinceIncome золота; выплачено жалованье наместникам: $totalStewardWages. " +
                when {
                    unpaidStewardDays > 0 -> "$unpaidStewardDays выплат не удалось покрыть: верность соответствующих провинций снизилась."
                    totalStewardWages == 0 -> "В этот период жалованье наместникам не начислялось."
                    else -> "Все наместники получили жалованье."
                } + if (crisisSummary.isNotBlank()) " $crisisSummary" else ""
            val supplySummary = if (shortageDays == 0) {
                "Армия получила снабжение: $totalRationsUsed порций за $suppliedDays дн.; остаток провианта $provisions/100."
            } else {
                "Нехватка снабжения: $shortageDays дн.; потеряно $totalSoldiersLost солдат, снизились доверие гвардии и верность провинций."
            }
            val eventsSummary = if (worldEventNotes.isEmpty()) "" else " События королевства: ${worldEventNotes.joinToString(" ")}"
            realmReport = "$supplySummary $provinceReport$eventsSummary"
            addJournalEntry("Ежедневный отчёт королевства: $realmReport")
            addJournalEntry("Провинции: $provinceReport")
        }
        worldHour = clockAdvance.targetHour
        val phase = clockAdvance.phase.label
        val timeEvent = clockAdvance.timeMessage
        worldEvent = if (worldEventNotes.isEmpty()) timeEvent else "$timeEvent Последнее событие королевства: ${worldEventNotes.last()}"
        addJournalEntry("Время: день $worldDay, ${worldHour.toString().padStart(2, '0')}:00. $worldEvent")
        save()
    }
    fun travelToLocation(destination: String, requestedHours: Int? = null) {
        when (val plan = WorldManager.planTravel(
            currentLocation = currentLocation,
            destination = destination,
            sceneIndex = sceneIndex,
            roseQuestStage = roseQuestStage,
            requestedHours = requestedHours
        )) {
            is TravelPlan.Rejected -> {
                realmReport = plan.reason
                lastOutcome = plan.reason
                addJournalEntry("Путешествие не состоялось: ${plan.reason}")
                save()
            }
            is TravelPlan.Ready -> {
                val route = TravelManager.findRoute(plan.origin, plan.destination)
                if (route == null) {
                    val reason = "Между этими местами пока не найден безопасно рассчитанный маршрут."
                    realmReport = reason
                    lastOutcome = reason
                    addJournalEntry("Путешествие не состоялось: $reason")
                    save()
                } else {
                    val departureDay = worldDay
                    val departureHour = worldHour
                    currentLocation = plan.destination
                    advanceWorldTime(route.totalHours)
                    val nightWarning = if (worldHour >= 22 || worldHour <= 5) " Сейчас ночь: на улицах мало людей, а патрули проверяют путников." else ""
                    val roadNote = if (route.isDangerous) {
                        " Маршрут включает ${route.dangerousSegments} опасных участк(а/ов); сохраняй бдительность."
                    } else {
                        " Весь путь проходит по спокойным дорогам."
                    }
                    val encounter = RandomEncounterManager.resolve(route, departureDay, departureHour, encounterCount)
                    val encounterNote = when (val event = encounter.encounter) {
                        TravelEncounter.None -> ""
                        is TravelEncounter.Merchant -> {
                            encounterCount++
                            gold = (gold + event.goldReward).coerceAtMost(1_000_000)
                            val itemAdded = addInventoryItem(event.itemReward)
                            val itemNote = if (itemAdded) " Также получено: ${event.itemReward}." else " Место для припасов не нашлось."
                            addJournalEntry("Дорожная встреча: ${event.description} Получено золота: ${event.goldReward}.$itemNote")
                            " ${event.description} Получено ${event.goldReward} золота.$itemNote"
                        }
                        is TravelEncounter.Traveler -> {
                            encounterCount++
                            addJournalEntry("Дорожная встреча: ${event.description}")
                            " ${event.description}"
                        }
                        is TravelEncounter.Ambush -> {
                            encounterCount++
                            addJournalEntry("Засада в пути: ${event.enemyName}. ${event.description}")
                            beginBattle(event.enemyName, event.enemyHealth)
                            " ${event.description}"
                        }
                    }
                    realmReport = "Ты прибыл: ${plan.destination}. Путь: ${route.routeText}. Дорога заняла ${route.totalHours} ч.$roadNote$nightWarning$encounterNote"
                    addJournalEntry("Путешествие: ${route.routeText} (${route.totalHours} ч.; опасных участков: ${route.dangerousSegments}).")
                    save()
                }
            }
        }
    }
    val timePhase = when (worldHour) { in 5..10 -> "Утро"; in 11..16 -> "День"; in 17..20 -> "Вечер"; else -> "Ночь" }
    val scene = scenes[sceneIndex]
    // These reference pages are workspaces, not illustrated sidebars: use the full book spread.
    val fullWidthPages = selectedPage in setOf("world", "realm_map", "items", "people", "companions", "dialogues")
    val availableChoices = buildList {
        addAll(scene.choices)
        if (sceneIndex == 5 && guard >= 30) add(GameChoice("Потребовать сопровождение гвардии", "Открывается благодаря доверию капитана", "Гаррет выделяет двух надёжных стражников. Их присутствие заставляет городских осведомителей говорить осторожнее, но безопаснее.", guard=4, reputation=1, nextScene=6))
        if (sceneIndex == 5 && seleste >= 15) add(GameChoice("Передать тайное послание Селесты", "Открывается благодаря доверию леди", "Посланник Селесты приносит знак допуска к закрытому причалу. На обороте записано имя человека, видевшего наследника.", seleste=3, rose=2, nextScene=8))
        if (sceneIndex == 9 && influence >= 8) add(GameChoice("Созвать собственных осведомителей", "Открывается при высоком влиянии", "Твои связи позволяют собрать людей из разных кварталов. В обмен на обещание защиты они соглашаются следить за ночными перевозками.", gold=-2, influence=2, commoners=2, nextScene=10))
        if (sceneIndex == 9 && hasInventoryItem("Медный ключ без короны")) add(GameChoice("Использовать медный ключ у каменной двери", "Открывается, если ключ найден ранее", "Ключ поворачивается с сухим щелчком. За дверью обнаруживается лестница в старое убежище, а на стене — карта семи домов.", rose=4, influence=3, nextScene=10))
    }
    Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF493423), Night, Color(0xFF0C0908)))).padding(outerPadding)) {
        Column(Modifier.fillMaxSize()) {
            // Верхняя шапка в точности повторяет композицию референса: знак игры, год, глава и место.
            Row(Modifier.fillMaxWidth().height(headerHeight), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(if (ultraCompact) 5.dp else 12.dp)) {
                Column(Modifier.width(brandWidth).fillMaxHeight().background(Brush.horizontalGradient(listOf(Color(0xFF170D09), Color(0xFF3A2115))), RoundedCornerShape(5.dp)).border(2.dp, Color(0xFF8C673A), RoundedCornerShape(5.dp)).padding(start=12.dp), verticalArrangement = Arrangement.Center) {
                    Text(if (ultraCompact) "♛ ПЕПЕЛ" else "♛  ПЕПЕЛ", color = Color(0xFFD6B16F), fontSize = if (ultraCompact) 10.sp else if (compactLandscape) 13.sp else 17.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, letterSpacing = 1.sp)
                    Text(if (ultraCompact) "КОРОНЫ" else "       КОРОНЫ", color = Paper, fontSize = if (ultraCompact) 9.sp else if (compactLandscape) 11.sp else 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif, letterSpacing = 1.sp)
                }
                Row(Modifier.weight(1f).fillMaxHeight().background(Color(0xFF21140E), RoundedCornerShape(5.dp)).border(2.dp, Color(0xFF8C673A), RoundedCornerShape(5.dp)).padding(horizontal = if (ultraCompact) 5.dp else 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceEvenly) {
                    Text("▦ 1327 · День $worldDay · ${worldHour.toString().padStart(2, '0')}:00", modifier = Modifier.weight(1f), color = Paper, fontSize = if (ultraCompact) 7.sp else if (compactLandscape) 8.sp else 10.sp, fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!ultraCompact) Text("│", color = Gold, fontSize = 18.sp)
                    Text(if (selectedPage == "story") scene.chapter else "ЛЕТОПИСЬ ЭСТЕРВАЛЯ", modifier = Modifier.weight(1f), color = Paper, fontSize = if (ultraCompact) 7.sp else 9.sp, fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                    if (!ultraCompact) Text("│", color = Gold, fontSize = 18.sp)
                    Text("⌖ ${if (selectedPage == "story") scene.place else currentLocation}", modifier = Modifier.weight(1f), color = Paper, fontSize = if (ultraCompact) 7.sp else if (compactLandscape) 8.sp else 10.sp, fontFamily = FontFamily.Serif, maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
                OutlinedButton(onClick = { selectedPage = "about" }, modifier = Modifier.width(if (ultraCompact) 36.dp else 62.dp).fillMaxHeight(), border = BorderStroke(2.dp, Color(0xFF8C673A)), colors = ButtonDefaults.outlinedButtonColors(containerColor = Color(0xFF21140E), contentColor = Gold), shape = RoundedCornerShape(5.dp), contentPadding = PaddingValues(0.dp)) { Text("⚙", fontSize = 19.sp) }
            }
            Spacer(Modifier.height(7.dp))
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(columnGap)) {
                // Узкая книжная навигация вместо перегруженной верхней ленты.
                Column(Modifier.width(leftRailWidth).fillMaxHeight().background(Brush.verticalGradient(listOf(Color(0xFF5B4028), Color(0xFF241710))), RoundedCornerShape(5.dp)).border(2.dp, Gold, RoundedCornerShape(5.dp)).padding(4.dp), verticalArrangement = Arrangement.spacedBy(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    listOf("✥" to "story", "♧" to "world", "▤" to "journal", "♜" to "items", "♟♟" to "people", "⚖" to "quests", "⋯" to "more").forEach { (icon, key) ->
                        val target = key
                        androidx.compose.material3.OutlinedButton(
                            onClick = { selectedPage = target },
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(2.dp),
                            border = BorderStroke(1.dp, if (selectedPage == target) Gold else Color(0xFF705337)),
                            colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selectedPage == target) Color(0xFFB88D50) else Color(0xFF241912), contentColor = Paper),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                                Text(icon, fontSize = if (ultraCompact) 17.sp else 23.sp, textAlign = TextAlign.Center)
                                if (!ultraCompact) Text(when (key) { "story" -> "Сюжет"; "world" -> "Карта"; "journal" -> "Летопись"; "items" -> "Сумка"; "people" -> "Люди"; "quests" -> "Задания"; else -> "Ещё" }, fontSize = if (compactLandscape) 8.sp else 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 0.2.sp, textAlign = TextAlign.Center, maxLines = 1)
                            }
                        }
                    }
                }
                Row(Modifier.weight(1f).fillMaxHeight(), horizontalArrangement = Arrangement.spacedBy(columnGap)) {
                Column(Modifier.weight(if (fullWidthPages) 1f else if (ultraCompact) 1.04f else 0.96f).fillMaxHeight().background(Brush.verticalGradient(listOf(Color(0xFFF4E7CC), Color(0xFFE8D5AE), Color(0xFFD8BF91))), RoundedCornerShape(5.dp)).border(4.dp, Color(0xFF68482A), RoundedCornerShape(5.dp)).padding(3.dp).border(1.dp, Color(0xFFB18A55), RoundedCornerShape(3.dp)).padding(if (ultraCompact) 6.dp else if (compactLandscape) 9.dp else 12.dp).verticalScroll(rememberScrollState())) {
    @Composable
    fun RenderPage_realm_map() {
        PaperHeading("КАРТА КОРОЛЕВСТВА ЭСТЕРВАЛЬ")
        Text("Иллюстрированная карта владений. Выбери место на карте: дорога займёт время, а прибытие будет записано в летопись.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(8.dp))
        RealmIllustratedMap(currentLocation = currentLocation, sceneIndex = sceneIndex, roseQuestStage = roseQuestStage, onTravel = { place, hoursCost ->
            travelToLocation(place, hoursCost)
        })
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().background(Color(0xFFD4C096), RoundedCornerShape(5.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text("ТЕКУЩЕЕ МЕСТО", color = Muted, fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
                Text(currentLocation, color = Ink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            }
            Text("ДЕНЬ $worldDay · ${worldHour.toString().padStart(2, '0')}:00", color = Ink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(6.dp))
        Text(realmReport, color = Ink, fontSize = 11.sp, lineHeight = 16.sp)
        Text("Легенда: ✦ столица  ·  ♜ крепость  ·  ⚓ порт  ·  ♧ сады  ·  ⚒ город", color = Muted, fontSize = 9.sp)
    }
    @Composable
    fun RenderPage_frontier() {
        PaperHeading("ГЛАВА III · СЕВЕРНЫЙ ПОХОД")
        Text("За перевалом лежит марка Вальден — последний рубеж перед древней дорогой к столице. После решений Совета северные дома требуют гарантий, а разведчики сообщают о людях Чёрной розы среди наёмников.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp)) {
                Text("МАРКА ВАЛЬДЕН · СЕВЕРНЫЕ ЗЕМЛИ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text("Этап похода: $frontierStage / 3", color = Ink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text("Угроза на тракте: $frontierThreat / 100", color = Ink, fontSize = 12.sp)
                LinearProgressIndicator(progress = { frontierThreat / 100f }, modifier = Modifier.fillMaxWidth().height(6.dp), color = Color(0xFF876039), trackColor = Color(0xFFB99E72))
                Spacer(Modifier.height(7.dp))
                Text(frontierReport, color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        if (housesQuestStage < 3) {
            Text("Сначала заверши главу Совета семи домов: северные лорды не станут вести переговоры с неизвестным посланником.", color = Color(0xFF8B382B), fontSize = 12.sp)
            Button(onClick = { selectedPage = "quests" }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Перейти к заданиям", color = Paper) }
        } else when (frontierStage) {
            0 -> {
                Text("I · ПЕРЕВАЛ ВОРОНОВ. Разведчики нашли три пути к крепости: открытая дорога, старый шахтёрский тоннель и переговоры с кланом горцев.", color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
                Button(onClick = {
                    if (gold >= 4) {
                        frontierRoute = 1; frontierThreat = (frontierThreat - 12).coerceAtLeast(0); guard = (guard + 3).coerceAtMost(100); gold -= 4; frontierStage = 1
                        frontierReport = "Гаррет провёл отряд по открытой дороге. Дозорные Вальдена увидели королевский знак и пропустили вас, но запасы пришлось пополнить."
                        advanceWorldTime(6); addJournalEntry("Северный поход: выбран открытый путь через Перевал Воронов."); save()
                    } else frontierReport = "Для прохода по открытой дороге и снабжения отряда требуется 4 золота. Выбери другой маршрут или собери средства."
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Открытая дорога · поддержка гвардии · 4 золота", color = Paper) }
                Button(onClick = { frontierRoute = 2; influence = ResourceRules.addInfluence(influence, 3); health = (health - 8).coerceAtLeast(1); frontierStage = 1; frontierReport = "Ты провёл людей через шахтёрский тоннель. В глубине обнаружен тайный склад снаряжения и следы недавнего присутствия наёмников."; addUniqueQuestItem("Шахтёрская карта Вальдена"); advanceWorldTime(8); addJournalEntry("Северный поход: тайный путь через шахту."); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Старый тоннель · риск и тайна", color = Paper) }
                Button(onClick = { frontierRoute = 3; peopleRep = FactionManager.applyDelta(peopleRep, 4); setCompanionBond("Мира", companionBondMira + 2); frontierStage = 1; frontierReport = "Мира помогла найти старейшину горцев. Клан согласился провести отряд через перевал, если корона защитит их зимние поселения."; addInventoryItem("Знак клана горцев"); advanceWorldTime(5); addJournalEntry("Северный поход: заключено соглашение с горцами."); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Переговоры с горцами · союз", color = Paper) }
            }
            1 -> {
                Text("II · ДЕРЕВНЯ СЕРОГО БРОДА. На деревню напали люди в одинаковых серых плащах. Среди них замечен знак Чёрной розы. Реши, что важнее: жители, доказательства или быстрый удар.", color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
                Button(onClick = { frontierThreat = (frontierThreat - 18).coerceAtLeast(0); peopleRep = FactionManager.applyDelta(peopleRep, 6); health = (health - 5).coerceAtLeast(1); frontierStage = 2; frontierReport = "Ты вывел жителей из горящих домов. Староста передал имена пропавших людей и ключ от старого дозорного поста."; addInventoryItem("Список пропавших жителей"); advanceWorldTime(4); addJournalEntry("Северный поход: спасены жители Серого Брода."); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Спасти жителей · доверие народа", color = Paper) }
                Button(onClick = { frontierThreat = (frontierThreat - 8).coerceAtLeast(0); influence = ResourceRules.addInfluence(influence, 5); roseRep = FactionManager.applyDelta(roseRep, 3); frontierStage = 2; frontierReport = "Ты захватил связного и добыл шифр приказов. Деревня понесла потери, но теперь известен следующий пункт тайной сети."; addUniqueQuestItem("Зашифрованный приказ Чёрной розы"); advanceWorldTime(3); addJournalEntry("Северный поход: захвачен связной Чёрной розы."); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Захватить связного · добыть улики", color = Paper) }
                Button(onClick = {
                    if (gold >= 5) {
                        frontierThreat = (frontierThreat - 25).coerceAtLeast(0); reputation = (reputation + 2).coerceAtMost(100); gold -= 5; frontierStage = 2
                        frontierReport = "Гаррет повёл внезапную атаку. Нападавшие разбиты, но часть улик уничтожена в суматохе."
                        advanceWorldTime(2); addJournalEntry("Северный поход: проведена внезапная атака."); save()
                    } else frontierReport = "Для подготовки немедленного штурма требуется 5 золота. Этап похода не изменился."
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Немедленный штурм · военная сила · 5 золота", color = Paper) }
            }
            2 -> {
                Text("III · КРЕПОСТЬ ВАЛЬДЕН. Внутренние ворота открыты, но командир крепости утверждает, что действует по королевскому приказу. На столе лежит документ с печатью, которую ты уже видел в архиве.", color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
                Button(onClick = { frontierStage = 3; crownRep = FactionManager.applyDelta(crownRep, 6); influence = ResourceRules.addInfluence(influence, 4); gold = ResourceRules.addGold(gold, 12); grantExperience(100); addUniqueQuestItem("Приказ регента о северной смуте"); frontierReport = "Ты предъявил улики и добился признания командира. Приказ регента доказывает, что мятеж на севере был спровоцирован из столицы. Крепость признаёт твоё право говорить от имени короны."; addJournalEntry("Глава III завершена: раскрыта провокация регента в Марке Вальден."); advanceWorldTime(3); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Предъявить доказательства · раскрыть заговор", color = Ink) }
                Button(onClick = { frontierStage = 3; guard = (guard + 8).coerceAtMost(100); reputation = (reputation + 4).coerceIn(-100, 100); gold = ResourceRules.addGold(gold, 8); grantExperience(100); addUniqueQuestItem("Знамя гарнизона Вальдена"); frontierReport = "Ты принял вызов командира и победил в поединке. Гарнизон подчинился силе, но часть дворян считает тебя опасным человеком."; addJournalEntry("Глава III завершена: крепость Вальден подчинена после поединка."); advanceWorldTime(2); save() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Вызвать командира на поединок · честь и сила", color = Paper) }
            }
            else -> {
                Text("МАРКА ВАЛЬДЕН ПОД КОНТРОЛЕМ", color = Color(0xFF527044), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Text("Твой путь: ${when(frontierRoute) { 1 -> "королевская дорога"; 2 -> "шахтёрский тоннель"; else -> "союз с горцами" }}. Северные владения теперь связаны с твоими решениями. В летописи сохранена развязка главы.", color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
                Button(onClick = { selectedPage = "journal" }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Открыть летопись", color = Paper) }
            }
        }
    }
    @Composable
    fun RenderPage_battle() {
        PaperHeading("БОЕВОЙ ЖУРНАЛ · СТАЛЬ И ВОЛЯ")
        Text("Пошаговые схватки. Выбирай между быстрым ударом, тяжёлой атакой, защитой и лечением. Противники опасны: не вступай в бой с низким здоровьем.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Text(if (worldDay < woundedUntilDay) "РАНЕНИЕ: до следующего игрового дня атака −3, входящий урон +2." else "Состояние: боевых ранений нет.", color = if (worldDay < woundedUntilDay) Color(0xFF8B3028) else Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text("Награды за обычные победы сегодня: $battleRewardsToday/4 · Босс получает отдельную уникальную награду.", color = Muted, fontSize = 11.sp)
        Spacer(Modifier.height(10.dp))
        Text("ТВОЁ СНАРЯЖЕНИЕ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val equipment = EquipmentManager.profile(inventory)
        Text("Оружие: ${equipment.weaponName} (+${equipment.attackBonus})\nЗащита: ${equipment.armorName} (+${equipment.armorBonus} к защите)", color = Ink, fontSize = 12.sp)
        Spacer(Modifier.height(10.dp))
        if (enemyName.isBlank()) {
            Text("ОРУЖЕЙНАЯ · ПОКУПКА СНАРЯЖЕНИЯ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            if (!hasInventoryItem("Охотничий клинок")) Button(onClick = { if (gold >= 10) { if (addInventoryItem("Охотничий клинок")) { gold -= 10; battleLog = "Куплен охотничий клинок (+3 к атаке)." } else battleLog = lastOutcome ?: "Не удалось добавить предмет."; save() } else battleLog = "Не хватает золота: нужно 10." }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Охотничий клинок · 10 золота", color = Paper) }
            if (!hasInventoryItem("Тяжёлый палаш")) Button(onClick = { if (gold >= 28) { if (addInventoryItem("Тяжёлый палаш")) { gold -= 28; battleLog = "Куплен тяжёлый палаш (+7 к атаке)." } else battleLog = lastOutcome ?: "Не удалось добавить предмет."; save() } else battleLog = "Не хватает золота: нужно 28." }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Тяжёлый палаш · 28 золота", color = Paper) }
            Text("ВЫБЕРИ ПРОТИВНИКА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            Button(onClick = { startRandomEncounter() }, colors = ButtonDefaults.buttonColors(containerColor = Gold), modifier = Modifier.fillMaxWidth()) { Text("🎲 Случайная встреча в пути", color = Ink) }
            Button(onClick = { beginBattle("Древний рыцарь Чёрной розы", 105) }, enabled = !bossDefeated, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A2020)), modifier = Modifier.fillMaxWidth()) { Text(if (bossDefeated) "✓ Древний рыцарь повержен · награда получена" else "☠ БОСС: Древний рыцарь · 105 HP", color = Paper) }
            Button(onClick = { beginBattle("Городской головорез", 32) }, colors = ButtonDefaults.buttonColors(containerColor = Leather), modifier = Modifier.fillMaxWidth()) { Text("Городской головорез · лёгкий бой", color = Paper) }
            Button(onClick = { beginBattle("Наёмник Чёрной розы", 48) }, colors = ButtonDefaults.buttonColors(containerColor = Leather), modifier = Modifier.fillMaxWidth()) { Text("Наёмник Чёрной розы · опасный бой", color = Paper) }
            Button(onClick = { beginBattle("Королевский изменник", 65) }, colors = ButtonDefaults.buttonColors(containerColor = Leather), modifier = Modifier.fillMaxWidth()) { Text("Королевский изменник · тяжёлый бой", color = Paper) }
        } else {
            Text("⚔ ПРОТИВНИК: $enemyName", color = Ink, fontSize = 17.sp, fontWeight = FontWeight.Bold, modifier = Modifier.animateContentSize().graphicsLayer { scaleX = enemyHitScale; scaleY = enemyHitScale })
            StatLine("Здоровье противника", enemyHealth, when { enemyName.contains("Древний рыцарь") -> 105; enemyName.contains("изменник") -> 65; enemyName.contains("Наёмник") -> 48; else -> 32 })
            StatLine("Твоё здоровье", health, 100)
            Spacer(Modifier.height(8.dp))
            Button(onClick = {
                val woundPenalty = BattleRules.woundAttackPenalty(worldDay < woundedUntilDay)
                val attackResult = BattleManager.playerAttack(
                    baseRoll = (9..14).random(), weaponBonus = EquipmentManager.attackBonus(inventory),
                    powerSkill = powerSkill, woundPenalty = woundPenalty, enemyHealth = enemyHealth
                )
                val attack = attackResult.damage
                battlePulse++
                enemyHealth = attackResult.enemyHealthAfter
                battleLog = "Ты наносишь быстрый удар на $attack урона.${if (woundPenalty > 0) " Ранение ослабляет удар на $woundPenalty." else ""}"
                if (enemyHealth == 0) winBattle() else enemyTurn()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Быстрый удар", color = Ink) }
            Button(onClick = {
                val woundPenalty = BattleRules.woundAttackPenalty(worldDay < woundedUntilDay)
                val attackResult = BattleManager.playerAttack(
                    baseRoll = (15..22).random(), weaponBonus = EquipmentManager.attackBonus(inventory),
                    powerSkill = powerSkill, woundPenalty = woundPenalty, enemyHealth = enemyHealth
                )
                val attack = attackResult.damage
                battlePulse++
                enemyHealth = attackResult.enemyHealthAfter
                battleLog = "Ты замахиваешься и наносишь тяжёлый удар на $attack урона.${if (woundPenalty > 0) " Ранение ослабляет удар на $woundPenalty." else ""}"
                if (enemyHealth == 0) winBattle() else enemyTurn(counterDamageBonus = 4)
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Тяжёлый удар · риск контратаки", color = Paper) }
            Button(onClick = { battleGuarding = true; battleLog = "Ты принимаешь защитную стойку."; enemyTurn() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Защищаться", color = Paper) }
            Button(onClick = {
                if (hasInventoryItem("Лечебный бальзам")) { removeInventoryItem("Лечебный бальзам"); health = (health + 30).coerceAtMost(100); battleLog = "Ты использовал бальзам и восстановил до 30 здоровья."; enemyTurn() }
                else battleLog = "Лечебного бальзама нет. Его можно купить у Миры в Нижнем городе."
                save()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Использовать бальзам (+30 HP)", color = Paper) }
            TextButton(onClick = {
                enemyName = ""
                enemyHealth = 0
                battleGuarding = false
                battleLog = "Ты отступил. Победа не засчитана."
                worldEvent = "Ты покинул место схватки и вернулся к дороге."
                save()
            }) { Text("Отступить", color = Muted) }
        }
        Spacer(Modifier.height(10.dp))
        Text("БОЕВОЙ ОТЧЁТ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(battleLog, color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
        Text("ПОБЕДЫ: $battleWins · ПОРАЖЕНИЯ: $battleDefeats", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
    @Composable
    fun RenderPage_dialogues() {
        PaperHeading("ЛИЧНЫЕ БЕСЕДЫ · ГОЛОСА ЭСТЕРВАЛЯ")
        Text("Выбери собеседника. Разговоры короткие, но ответы меняют доверие и могут открыть новые сведения. Персонажи помнят последний разговор.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        Spacer(Modifier.height(8.dp))
        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Гаррет Морн", "Селеста", "Мира", "Томас", "Герцог Рейнар").forEach { name ->
                Button(onClick = {
                    dialogNpc = name
                    val legacyNpc = prefs.getString("dialog_npc", "Гаррет Морн") ?: "Гаррет Морн"
                    dialogStage = prefs.getInt("dialog_stage_$name", if (legacyNpc == name) prefs.getInt("dialog_stage", 0) else 0).coerceIn(0, 3)
                    dialogTrust = prefs.getInt("dialog_trust_$name", if (legacyNpc == name) prefs.getInt("dialog_trust", 0) else 0).coerceIn(0, 20)
                    prefs.edit().putString("dialog_npc", name).putInt("dialog_stage", dialogStage).putInt("dialog_trust", dialogTrust).apply()
                }, contentPadding = PaddingValues(horizontal = 9.dp, vertical = 5.dp), colors = ButtonDefaults.buttonColors(containerColor = if (dialogNpc == name) Gold else Leather)) {
                    Text(name, color = if (dialogNpc == name) Ink else Paper, fontSize = 10.sp)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        val portrait = when (dialogNpc) {
            "Гаррет Морн" -> R.drawable.portrait_garrett
            "Селеста" -> R.drawable.portrait_celeste
            "Мира" -> R.drawable.portrait_mira
            "Томас" -> R.drawable.portrait_thomas
            else -> R.drawable.portrait_reynar
        }
        val npcInteraction = NpcInteractionManager.evaluate(dialogNpc, currentLocation, worldHour)
        val npcCanTalk = npcInteraction is NpcInteractionResult.Available
        Card(colors = CardDefaults.cardColors(containerColor = if (npcCanTalk) Color(0xFFE5D5B8) else Color(0xFFD9C6A8)), modifier = Modifier.fillMaxWidth()) {
            Text(NpcInteractionManager.message(npcInteraction), color = Ink, fontSize = 11.sp, lineHeight = 16.sp, modifier = Modifier.padding(9.dp))
        }
        val greeting = when (dialogNpc) {
            "Гаррет Морн" -> listOf("«Двери дворца закрыты, Вейл. Скажи, что ты ищешь — правду или неприятности?»", "«Я видел эту печать раньше. Но тот, кто её носил, давно числится мёртвым.»", "«Если решишь идти против Рейнара, сначала подумай, кто будет стоять за твоей спиной.»", "«На сегодня хватит. Доверие не выдают по первому требованию.»")
            "Селеста" -> listOf("«Ты снова пришёл без приглашения, Эдриан. Значит, дело действительно важное.»", "«Некоторые письма опаснее кинжалов. Особенно если знаешь, кто их написал.»", "«Я не прошу верить мне. Я прошу дать мне возможность доказать правду.»", "«Береги наши разговоры. У стен дворца слишком хороший слух.»")
            "Мира" -> listOf("«Если пришёл за покупками — плати. Если за слухами — сначала помоги людям.»", "«Лекарства исчезли не случайно. Кто-то скупает их для закрытого каравана.»", "«Я запомню, что ты не прошёл мимо. В этом городе это редкость.»", "«Возвращайся, когда рынок проснётся. У меня всегда найдётся работа.»")
            "Томас" -> listOf("«Ночью море говорит громче людей. Я слышал, как грузчики называли одно имя.»", "«В ту ночь фонарь на третьем причале погас раньше смены. Это был знак.»", "«Я могу показать место, но не бесплатно: мне нужна гарантия безопасности.»", "«Не заставляй меня жалеть, что я заговорил.»")
            else -> listOf("«Архивист решил заговорить с герцогом. Любопытно.»", "«Королевству нужен порядок. Иногда правда лишь мешает его восстановить.»", "«Я могу сделать тебя влиятельным. Но услуги двора никогда не бывают бесплатными.»", "«Подумай над предложением. Второго приглашения может не быть.»")
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.Top) {
            Image(painter = painterResource(portrait), contentDescription = dialogNpc, modifier = Modifier.width(112.dp).height(142.dp), contentScale = ContentScale.Crop)
            Column(Modifier.weight(1f)) {
                Text(dialogNpc.uppercase(), color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                Text(when(dialogNpc) { "Гаррет Морн" -> "Капитан королевской стражи"; "Селеста" -> "Дворянка · хранительница тайн"; "Мира" -> "Торговка Нижнего города"; "Томас" -> "Работник ночного порта"; else -> "Герцог · претендент на регентство" }, color = Muted, fontSize = 10.sp)
                Spacer(Modifier.height(8.dp))
                Text("« ${greeting[dialogStage]} »", color = Ink, fontSize = 13.sp, lineHeight = 19.sp, modifier = Modifier.animateContentSize())
            }
        }
        Spacer(Modifier.height(8.dp))
        Text("ТЕМА РАЗГОВОРА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val topics = listOf("Спросить о чёрной розе", "Обсудить пропавшего наследника", "Поговорить о доверии")
        topics.forEachIndexed { index, topic ->
            Button(onClick = {
                if (!NpcInteractionManager.isAvailable(dialogNpc, currentLocation, worldHour)) {
                    lastOutcome = NpcInteractionManager.message(NpcInteractionManager.evaluate(dialogNpc, currentLocation, worldHour))
                } else if (dialogStage < 3) {
                    dialogStage++
                    val delta = when(index) { 0 -> if (dialogNpc == "Селеста" || dialogNpc == "Герцог Рейнар") 2 else 1; 1 -> if (dialogNpc == "Гаррет Морн" || dialogNpc == "Томас") 2 else 1; else -> 1 }
                    dialogTrust = (dialogTrust + delta).coerceAtMost(20)
                    when(dialogNpc) { "Гаррет Морн" -> guard = (guard + delta).coerceAtMost(100); "Селеста" -> seleste = (seleste + delta).coerceAtMost(100); "Мира" -> miraBond = (miraBond + delta).coerceAtMost(100); "Томас" -> thomasBond = (thomasBond + delta).coerceAtMost(100); else -> reynarBond = (reynarBond + delta).coerceAtMost(100) }
                    addJournalEntry("Разговор с $dialogNpc: $topic.")
                    save()
                }
            }, enabled = dialogStage < 3 && npcCanTalk, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(8.dp), colors = ButtonDefaults.buttonColors(containerColor = if (index == 0) Gold else Leather)) {
                Text(topic, color = if (index == 0) Ink else Paper, fontSize = 11.sp)
            }
        }
        if (!npcCanTalk) Text("Личная беседа недоступна, пока персонаж не окажется рядом и не освободится.", color = Muted, fontSize = 10.sp)
        Text("Доверие в беседах с $dialogNpc: $dialogTrust / 20", color = Muted, fontSize = 10.sp)
        Text(if (dialogStage >= 3) "Эта беседа завершена. Персонаж уже поделился тем, что был готов рассказать." else "Темы разговора: ${3 - dialogStage} из 3 ещё доступны.", color = Muted, fontSize = 10.sp)
    }
    @Composable
    fun RenderPage_world() {
        PaperHeading("КАРТА КОРОЛЕВСТВА ЭСТЕРВАЛЬ")
        Text("Дороги королевства отмечены на старом пергаменте. Выбирай направление: некоторые места станут доступны по мере продвижения расследования.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(8.dp))
        val locationArt = when (currentLocation) {
            "Зимний сад" -> R.drawable.location_garden
            "Нижний город" -> R.drawable.city_locations_poor_district
            "Рыночная площадь" -> R.drawable.daily_life_market_day
            "Порт Веймар", "Порт Эстервала" -> R.drawable.travel_locations_harbor_approach
            "Старая часовня" -> R.drawable.religious_sites_variants_chapel_path
            "Старый мост" -> R.drawable.nature_locations_river_crossing
            "Северная застава" -> R.drawable.landscape_locations_winter_castle
            "Воронья деревня" -> R.drawable.travel_locations_village_road
            "Лесной тракт" -> R.drawable.travel_locations_forest_path
            "Крепость Вальден" -> R.drawable.landscape_locations_abandoned_fortress
            else -> R.drawable.location_castle
        }
        Box(Modifier.fillMaxWidth().height(116.dp).border(2.dp, Gold, RoundedCornerShape(5.dp))) {
            Image(painter = painterResource(locationArt), contentDescription = "Вид на $currentLocation", modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
            Box(Modifier.align(Alignment.BottomStart).fillMaxWidth().background(Brush.verticalGradient(listOf(Color.Transparent, Color(0xDD17110E)))).padding(horizontal = 10.dp, vertical = 7.dp)) {
                Text(currentLocation.uppercase(), color = Paper, fontSize = 12.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.5.sp)
            }
        }
        Spacer(Modifier.height(8.dp))
        Box(Modifier.fillMaxWidth().background(Color(0xFFD8C49B), RoundedCornerShape(6.dp)).border(2.dp, Color(0xFF89683E), RoundedCornerShape(6.dp)).padding(8.dp)) {
            Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("✥  СЕВЕРНЫЕ ЗЕМЛИ  ✥", color = Muted, fontSize = 9.sp, letterSpacing = 2.sp)
                Spacer(Modifier.height(6.dp))
                MapPlace("🏰 Королевский замок", currentLocation == "Королевский замок", true, "Главная крепость Эстерваль") { travelToLocation("Королевский замок") }
                Text("│   ╲", color = Muted, fontSize = 18.sp)
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    MapPlace("🌿 Зимний сад", currentLocation == "Зимний сад", sceneIndex >= 0, "Уединённое место Селесты") { travelToLocation("Зимний сад") }
                    MapPlace("🌉 Старый мост", currentLocation == "Старый мост", sceneIndex >= 5, "Тайная каменная дверь") { travelToLocation("Старый мост") }
                }
                Text("╲       │       ╱", color = Muted, fontSize = 18.sp)
                MapPlace("⚒ Нижний город", currentLocation == "Нижний город", sceneIndex >= 5, "Рынок и ремесленные кварталы") { travelToLocation("Нижний город") }
                Text("│", color = Muted, fontSize = 18.sp)
                MapPlace("⚓ Порт Веймар", currentLocation == "Порт Веймар", sceneIndex >= 7, "Причалы и ночные поставки") { travelToLocation("Порт Веймар") }
                MapPlace("⛪ Старая часовня", currentLocation == "Старая часовня", roseQuestStage >= 1, "Заброшенная часовня за портовой дорогой") { travelToLocation("Старая часовня") }
                Spacer(Modifier.height(8.dp))
                Text("✥  ЗА ПРЕДЕЛАМИ ГОРОДА  ✥", color = Muted, fontSize = 9.sp, letterSpacing = 1.5.sp)
                MapPlace("🛡 Северная застава", currentLocation == "Северная застава", sceneIndex >= 5, "Пограничный пост на старом тракте") { travelToLocation("Северная застава") }
                MapPlace("🌾 Воронья деревня", currentLocation == "Воронья деревня", sceneIndex >= 5, "Деревня у холмов, где жители боятся леса") { travelToLocation("Воронья деревня") }
                MapPlace("🌲 Лесной тракт", currentLocation == "Лесной тракт", sceneIndex >= 5, "Заброшенная дорога между деревней и заставой") { travelToLocation("Лесной тракт") }
                Spacer(Modifier.height(4.dp)); Text("≈≈≈  РЕКА ЭСТЕР  ≈≈≈", color = Color(0xFF526C70), fontSize = 10.sp, letterSpacing = 2.sp)
            }
        }
        Spacer(Modifier.height(9.dp))
        Text("ТЕКУЩЕЕ МЕСТО: $currentLocation", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        Text("Путешествия записываются в летопись. Недоступные места откроются после новых событий.", color = Muted, fontSize = 11.sp)
        if (currentLocation in listOf("Северная застава", "Воронья деревня", "Лесной тракт")) {
            Spacer(Modifier.height(8.dp))
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(10.dp)) {
                    Text("СОБЫТИЕ В ПУТИ", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    val travelDescription = when (currentLocation) {
                        "Северная застава" -> "Дозорный сообщает о пропавшем патруле. На снегу у ворот виден след чёрного воска."
                        "Воронья деревня" -> "Староста просит найти пропавшего сборщика податей. Жители подозревают людей в гербовых плащах."
                        else -> "У обочины лежит сломанная телега. В грязи заметны следы, ведущие к старому лесному святилищу."
                    }
                    Text(travelDescription, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                    Spacer(Modifier.height(6.dp))
                    val investigationBit = when (currentLocation) {
                        "Северная застава" -> 1
                        "Воронья деревня" -> 2
                        else -> 4
                    }
                    val investigationDone = (worldInvestigationFlags and investigationBit) != 0
                    Button(onClick = {
                        if (!investigationDone) {
                            val clue = when (currentLocation) {
                                "Северная застава" -> "Осколок печати заставы"
                                "Воронья деревня" -> "Список собранных податей"
                                else -> "Знак лесного святилища"
                            }
                            if (!hasInventoryItem(clue)) addInventoryItem(clue)
                            influence = (influence + 1).coerceAtMost(100)
                            worldInvestigationFlags = worldInvestigationFlags or investigationBit
                            addJournalEntry("Исследование: $currentLocation — получено: $clue.")
                            worldEvent = "Находка в районе: $currentLocation. Новая улика добавлена в инвентарь."
                        } else {
                            worldEvent = "В районе $currentLocation ты уже всё осмотрел. Новых улик не найдено."
                        }
                        save()
                    }, enabled = !investigationDone, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (investigationDone) "✓ Место осмотрено" else "Осмотреть место и забрать улику", color = Paper, fontSize = 11.sp) }
                }
            }
        }
    }
    @Composable
    fun RenderPage_council() {
        PaperHeading("СОВЕТ СЕМИ ДОМОВ · БОРЬБА ЗА БУДУЩЕЕ ЭСТЕРВАЛЯ")
        Text("Три заседания определят временный порядок в королевстве. Каждый голос меняет баланс сил: поддержка короны укрепляет гвардию, помощь горожанам снижает напряжение, а тайные сделки усиливают влияние Чёрной розы.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(9.dp))
        StatLine("Политический баланс", (50 + councilBalance).coerceIn(0,100), 100)
        Text("ЗАСЕДАНИЕ ${councilProgress + 1} / 3", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Text(councilReport, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        if (councilProgress < 3) {
            Button(onClick = {
                councilProgress++; councilBalance = (councilBalance + 5).coerceAtMost(20); crownRep = FactionManager.applyDelta(crownRep, 3); guard = (guard + 2).coerceAtMost(100); influence = ResourceRules.addInfluence(influence, 1)
                councilReport = "Ты предложил укрепить королевскую гвардию и ограничить полномочия временного регента. Капитан Гаррет поддержал план, но торговые дома потребовали гарантий. Совет продолжится на следующем заседании."
                addJournalEntry("Совет семи домов: поддержано укрепление гвардии."); save()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Укрепить гвардию и законный порядок", color = Paper, fontSize = 11.sp) }
            Button(onClick = {
                if (gold >= 3) {
                    councilProgress++; councilBalance = (councilBalance - 2).coerceAtLeast(-20); peopleRep = (peopleRep + 5).coerceAtMost(100); gold -= 3
                    councilReport = "Ты потребовал отменить чрезвычайные сборы и открыть зерновые склады. Народные представители поддержали решение. Казна потеряла три золотых, зато улицы стали спокойнее."
                    addJournalEntry("Совет семи домов: поддержана помощь горожанам."); save()
                } else councilReport = "Для открытия складов и облегчения податей требуется 3 золота. Заседание не засчитано."
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Открыть склады и облегчить подати · −3 золота", color = Paper, fontSize = 11.sp) }
            Button(onClick = {
                councilProgress++; councilBalance = (councilBalance - 5).coerceAtLeast(-20); roseRep = FactionManager.applyDelta(roseRep, 5); influence = ResourceRules.addInfluence(influence, 4)
                councilReport = "Ты заключил негласную сделку с представителем старого рода. Совет принял твоё предложение, но в зале погасли две свечи. Кто-то понял, с кем ты разговаривал."
                addJournalEntry("Совет семи домов: заключена тайная политическая сделка."); save()
            }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF5A3028))) { Text("Заключить тайную сделку с неизвестным домом", color = Paper, fontSize = 11.sp) }
        } else {
            Text("ИТОГ СОВЕТА", color = Muted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
            Text(when { councilBalance >= 8 -> "Гвардия и сторонники порядка получили преимущество. Регенту придётся считаться с законом."; councilBalance <= -5 -> "Народные представители и тайные союзы изменили расстановку сил. В столице назревают перемены."; else -> "Ни одна сторона не получила решающего перевеса. Семь домов вынуждены искать компромисс." }, color = Ink, fontSize = 13.sp, lineHeight = 18.sp)
            Text("Все три заседания завершены. Новые решения Совета откроются в следующей главе истории.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
        }
    }
    @Composable
    fun RenderPage_companions() {
        PaperHeading("СПУТНИКИ · ДОВЕРИЕ И СОВМЕСТНЫЕ ПУТЕШЕСТВИЯ")
        Text("Выбери одного спутника для экспедиции. Его характер влияет на исход пути и на отношения. Спутники не бессмертны: рискованные решения могут стоить здоровья и доверия.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(8.dp))
        listOf("Гаррет Морн" to "Военный опыт: снижает риск ранений.", "Селеста" to "Дипломатия: помогает находить ценные сведения.", "Мира" to "Знание города: чаще приносит припасы и золото.", "Томас" to "Морские связи: открывает редкие находки.").forEach { (name, skill) ->
            Card(colors = CardDefaults.cardColors(containerColor = if (companion == name) Color(0xFFD4BE91) else Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), onClick = { companion = name; save() }) {
                Column(Modifier.padding(9.dp)) { Text((if (companion == name) "✓ " else "") + name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp); Text(skill, color = Muted, fontSize = 10.sp) }
            }
        }
        Text("СПУТНИК: $companion · ДОВЕРИЕ: ${selectedCompanionBond()}", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        val personalMoment = CompanionManager.personalMoment(companion)
        val companionAvailability = NpcInteractionManager.evaluate(companion, currentLocation, worldHour)
        val canHavePersonalMoment = companionAvailability is NpcInteractionResult.Available
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth().padding(vertical = 7.dp)) {
            Column(Modifier.padding(10.dp)) {
                Text("ЛИЧНЫЙ МОМЕНТ", color = Leather, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text(personalMoment.prompt, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                Text(
                    when {
                        companionTalkDay == worldDay -> "Сегодня вы уже поговорили по душам."
                        !canHavePersonalMoment -> NpcInteractionManager.message(companionAvailability)
                        else -> "На такую беседу нужно время и внимание. Один личный разговор в день."
                    }, color = Muted, fontSize = 10.sp
                )
                listOf(
                    CompanionApproach.SUPPORT to personalMoment.supportLabel,
                    CompanionApproach.ASK_ABOUT_PAST to personalMoment.curiosityLabel,
                    CompanionApproach.HUMOR to personalMoment.humorLabel
                ).forEach { (approach, label) ->
                    Button(onClick = {
                        if (companionTalkDay != worldDay && NpcInteractionManager.isAvailable(companion, currentLocation, worldHour)) {
                            val moment = CompanionManager.resolvePersonalMoment(companion, approach, selectedCompanionBond())
                            setCompanionBond(companion, moment.bondAfter)
                            companionTalkDay = worldDay
                            companionMomentReport = moment.response
                            addJournalEntry("Личный разговор с ${moment.companionName}: $companionMomentReport")
                            save()
                        }
                    }, enabled = companionTalkDay != worldDay && canHavePersonalMoment, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                        Text(label, color = Paper, fontSize = 10.sp)
                    }
                }
                Text(companionMomentReport, color = Ink, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
        val storyStage = selectedCompanionStoryStage()
        val storyChapter = CompanionStoryManager.chapter(companion, storyStage)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEADFC9)), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Column(Modifier.padding(10.dp)) {
                Text("ЛИЧНАЯ ИСТОРИЯ · ГЛАВА ${storyStage + 1}/3", color = Leather, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                if (storyChapter == null) {
                    Text("${companion} завершил(а) свою личную историю. Между вами больше нет невысказанных обещаний.", color = Ink, fontSize = 12.sp)
                } else {
                    Text(storyChapter.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(storyChapter.scene, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                    Text("Требуемое доверие: ${storyChapter.minimumBond} · Сейчас: ${selectedCompanionBond()}", color = Muted, fontSize = 10.sp)
                    val storyReady = selectedCompanionBond() >= storyChapter.minimumBond && canHavePersonalMoment && companionTalkDay != worldDay
                    Text(if (companionTalkDay == worldDay) "Сегодня личное общение уже состоялось. Следующая глава откроется завтра." else "Одна важная личная беседа в день.", color = Muted, fontSize = 10.sp)
                    listOf(CompanionStoryChoice.SUPPORT to storyChapter.supportChoice, CompanionStoryChoice.INVESTIGATE to storyChapter.investigateChoice).forEach { (choice, label) ->
                        Button(onClick = {
                            if (companionTalkDay != worldDay && selectedCompanionBond() >= storyChapter.minimumBond && NpcInteractionManager.isAvailable(companion, currentLocation, worldHour)) {
                                val result = CompanionStoryManager.resolve(companion, selectedCompanionStoryStage(), selectedCompanionBond(), choice)
                                setCompanionBond(companion, result.bondAfter)
                                setCompanionStoryStage(companion, result.nextStage)
                                companionTalkDay = worldDay
                                companionStoryReport = result.report
                                addJournalEntry("Личная история ${result.companion}, глава ${result.previousStage + 1}: ${result.report}")
                                save()
                            }
                        }, enabled = storyReady, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                            Text(label, color = Paper, fontSize = 10.sp)
                        }
                    }
                }
                Text(companionStoryReport, color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
        val relationPair = CompanionRelationshipManager.pairs[(worldDay - 1).coerceAtLeast(0) % CompanionRelationshipManager.pairs.size]
        val relationScene = CompanionRelationshipManager.scene(relationPair)
        val relationPairAvailable = NpcInteractionManager.isAvailable(relationPair.first, currentLocation, worldHour) &&
            NpcInteractionManager.isAvailable(relationPair.second, currentLocation, worldHour)
        val relationScore = when (relationPair) {
            CompanionPair.GARRETT_CELESTE -> relationGarrettCeleste
            CompanionPair.GARRETT_MIRA -> relationGarrettMira
            CompanionPair.GARRETT_THOMAS -> relationGarrettThomas
            CompanionPair.CELESTE_MIRA -> relationCelesteMira
            CompanionPair.CELESTE_THOMAS -> relationCelesteThomas
            CompanionPair.MIRA_THOMAS -> relationMiraThomas
        }
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEADFC9)), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
            Column(Modifier.padding(10.dp)) {
                Text("ОТНОШЕНИЯ В КОМАНДЕ", color = Leather, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Общая атмосфера между спутниками: ${CompanionRelationshipManager.pairs.map { pair -> when (pair) { CompanionPair.GARRETT_CELESTE -> relationGarrettCeleste; CompanionPair.GARRETT_MIRA -> relationGarrettMira; CompanionPair.GARRETT_THOMAS -> relationGarrettThomas; CompanionPair.CELESTE_MIRA -> relationCelesteMira; CompanionPair.CELESTE_THOMAS -> relationCelesteThomas; CompanionPair.MIRA_THOMAS -> relationMiraThomas } }.average().toInt()}", color = Muted, fontSize = 10.sp)
                Text("${relationPair.first} ↔ ${relationPair.second}: $relationScore / 100", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                Text(relationScene.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(relationScene.description, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                Text(when {
                    companionRelationDay == worldDay -> "Сегодня ты уже помогал команде уладить разногласия."
                    relationPairAvailable -> "Оба спутника сейчас находятся рядом — можно вмешаться."
                    else -> "Для разговора оба спутника должны находиться рядом и быть свободны."
                }, color = Muted, fontSize = 10.sp)
                listOf(
                    CompanionRelationshipChoice.MEDIATE to "Помочь им найти компромисс",
                    CompanionRelationshipChoice.SUPPORT_FIRST to "Поддержать ${relationPair.first}",
                    CompanionRelationshipChoice.SUPPORT_SECOND to "Поддержать ${relationPair.second}"
                ).forEach { (choice, label) ->
                    Button(onClick = {
                        if (companionRelationDay != worldDay && NpcInteractionManager.isAvailable(relationPair.first, currentLocation, worldHour) && NpcInteractionManager.isAvailable(relationPair.second, currentLocation, worldHour)) {
                            val result = CompanionRelationshipManager.resolve(relationPair, relationScore, relationScore, choice)
                            setCompanionPairScore(relationPair, result.firstScore, result.secondScore)
                            companionRelationDay = worldDay
                            companionRelationReport = result.report
                            addJournalEntry("Разногласия спутников: ${result.report}")
                            save()
                        }
                    }, enabled = companionRelationDay != worldDay && relationPairAvailable, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                        Text(label, color = Paper, fontSize = 10.sp)
                    }
                }
                Text(companionRelationReport, color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
            }
        }
        val nextTeamEvent = CompanionTeamEventManager.events.firstOrNull { event ->
            !CompanionTeamEventManager.isCompleted(companionTeamEventFlags, event.id)
        }
        if (nextTeamEvent != null) {
            val eventPairScore = companionPairScore(nextTeamEvent.pair).first
            val firstHeroBond = companionBondFor(nextTeamEvent.pair.first)
            val secondHeroBond = companionBondFor(nextTeamEvent.pair.second)
            val eventUnlocked = CompanionTeamEventManager.isUnlocked(nextTeamEvent, eventPairScore, firstHeroBond, secondHeroBond)
            val eventPairAvailable = NpcInteractionManager.isAvailable(nextTeamEvent.pair.first, currentLocation, worldHour) &&
                NpcInteractionManager.isAvailable(nextTeamEvent.pair.second, currentLocation, worldHour)
            val eventReady = eventUnlocked && eventPairAvailable && companionTeamEventDay != worldDay
            Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFEADFC9)), modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                Column(Modifier.padding(10.dp)) {
                    Text("СОВМЕСТНАЯ ИСТОРИЯ · ${nextTeamEvent.pair.first.uppercase()} И ${nextTeamEvent.pair.second.uppercase()}", color = Leather, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    Text(nextTeamEvent.title, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(nextTeamEvent.scene, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                    Text(if (!eventUnlocked) "Требуется: отношения пары ${nextTeamEvent.minimumPairAffinity}+ и доверие каждого спутника ${nextTeamEvent.minimumHeroBond}+. Сейчас: $eventPairScore / $firstHeroBond / $secondHeroBond."
                        else if (!eventPairAvailable) "Оба спутника должны находиться рядом и быть свободны по расписанию."
                        else if (companionTeamEventDay == worldDay) "Сегодня уже состоялось совместное событие. Продолжение — завтра."
                        else "Оба спутника готовы к совместному делу.", color = Muted, fontSize = 10.sp)
                    listOf(
                        CompanionTeamEventChoice.COOPERATE to nextTeamEvent.cooperativeChoice,
                        CompanionTeamEventChoice.COMPROMISE to nextTeamEvent.pragmaticChoice
                    ).forEach { (choice, label) ->
                        Button(onClick = {
                            val stillAvailable = NpcInteractionManager.isAvailable(nextTeamEvent.pair.first, currentLocation, worldHour) &&
                                NpcInteractionManager.isAvailable(nextTeamEvent.pair.second, currentLocation, worldHour)
                            val currentPairScore = companionPairScore(nextTeamEvent.pair).first
                            val currentFirstBond = companionBondFor(nextTeamEvent.pair.first)
                            val currentSecondBond = companionBondFor(nextTeamEvent.pair.second)
                            if (companionTeamEventDay != worldDay && !CompanionTeamEventManager.isCompleted(companionTeamEventFlags, nextTeamEvent.id) &&
                                stillAvailable && CompanionTeamEventManager.isUnlocked(nextTeamEvent, currentPairScore, currentFirstBond, currentSecondBond)) {
                                val result = CompanionTeamEventManager.resolve(nextTeamEvent, currentPairScore, currentFirstBond, currentSecondBond, choice)
                                setCompanionPairScore(nextTeamEvent.pair, result.pairAffinityAfter, result.pairAffinityAfter)
                                setCompanionBond(nextTeamEvent.pair.first, result.firstHeroBondAfter)
                                setCompanionBond(nextTeamEvent.pair.second, result.secondHeroBondAfter)
                                companionTeamEventFlags = CompanionTeamEventManager.markCompleted(companionTeamEventFlags, result.eventId)
                                companionTeamEventDay = worldDay
                                companionTeamEventReport = result.report
                                addJournalEntry("Совместная история: ${nextTeamEvent.title}. ${result.report}")
                                save()
                            }
                        }, enabled = eventReady, modifier = Modifier.fillMaxWidth().padding(top = 3.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                            Text(label, color = Paper, fontSize = 10.sp)
                        }
                    }
                    Text(companionTeamEventReport, color = Muted, fontSize = 10.sp, lineHeight = 15.sp)
                }
            }
        } else {
            Text("Все шесть совместных историй команды завершены.", color = Muted, fontSize = 11.sp, modifier = Modifier.padding(vertical = 6.dp))
        }
        Button(onClick = {
            if (companionExpeditionDay == worldDay) {
                expeditionReport = "Сегодня вы уже совершили совместную экспедицию. Новая вылазка станет доступна завтра."
            } else {
                expeditionRuns++; advanceWorldTime(4)
                // Record the return day so an expedition crossing midnight cannot be repeated immediately.
                companionExpeditionDay = worldDay
                val expedition = CompanionManager.expedition(companion, selectedCompanionBond())
                health = (health + expedition.healthDelta).coerceAtLeast(1)
                gold = ResourceRules.addGold(gold, expedition.goldDelta)
                influence = ResourceRules.addInfluence(influence, expedition.influenceDelta)
                roseRep = FactionManager.applyDelta(roseRep, expedition.roseReputationDelta)
                setCompanionBond(companion, expedition.bondAfter)
                if (expedition.itemIsUnique) addUniqueQuestItem(expedition.itemReward)
                else addInventoryItem(expedition.itemReward)
                expeditionReport = expedition.report
                addJournalEntry("Экспедиция со спутником ${expedition.companionName}: $expeditionReport")
            }
            save()
        }, enabled = companionExpeditionDay != worldDay, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text(if (companionExpeditionDay == worldDay) "Экспедиция уже проведена сегодня" else "Отправиться в экспедицию · 4 часа", color = Ink) }
        Text(expeditionReport, color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
        Text("Совместных экспедиций: $expeditionRuns", color = Muted, fontSize = 10.sp)
    }
    @Composable
    fun RenderPage_houses() {
        PaperHeading("СЕМЬ ВЕЛИКИХ ДОМОВ · ПОЛИТИЧЕСКИЙ БАЛАНС")
        Text("Твои поступки постепенно меняют отношение влиятельных сил. Союзники могут открыть новые пути, а враги — закрыть ворота и начать охоту.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(12.dp))
        RelationCard("Королевская корона", "Сторонники порядка и регентства", (50 + crownRep).coerceIn(0,100), "Поддержка")
        RelationCard("Гильдии торговцев", "Купцы, перевозчики и банкиры", (50 + merchantRep).coerceIn(0,100), "Доверие")
        RelationCard("Простой народ", "Ремесленники, рабочие и жители", (50 + peopleRep).coerceIn(0,100), "Народная поддержка")
        RelationCard("Орден Чёрной розы", "Тайная сила из старых летописей", (50 + roseRep).coerceIn(0,100), "Влияние ордена")
        Text("Показатели отражают известное Эдриану отношение фракций, а не точное число сторонников.", color = Muted, fontSize = 11.sp)
    }
    @Composable
    fun RenderPage_life() {
        PaperHeading("ЖИЗНЬ ЭСТЕРВАЛЯ · ЛЮДИ И ДЕЛА")
        Text("Мир не замирает, пока Эдриан ищет правду. В разных местах его ждут знакомые лица, просьбы, сделки и слухи. NPC помнят важные разговоры.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(10.dp))
        Text("ТЕКУЩЕЕ МЕСТО: $currentLocation", color = Muted, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp)) {
                Text("ДЕНЬ $worldDay · ${worldHour.toString().padStart(2, '0')}:00 · $timePhase", color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(worldEvent, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                Spacer(Modifier.height(5.dp))
                Button(onClick = { advanceWorldTime(3) }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("⏳ Подождать 3 часа", color = Paper) }
            }
        }
        val npcSchedule = NpcScheduleManager.report(currentLocation, worldHour)
        Text("РАСПОРЯДОК ЖИТЕЛЕЙ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(npcSchedule.description, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        if (npcSchedule.availableNpcs.isNotEmpty()) {
            Text("Сейчас доступны: ${npcSchedule.availableNpcs.joinToString()}", color = Muted, fontSize = 10.sp)
        }
        Text("ГДЕ СЕЙЧАС ЗНАКОМЫЕ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val npcRoster = NpcScheduleManager.roster(worldHour)
        npcRoster.forEach { status ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(status.npcName, color = Ink, fontSize = 10.sp, modifier = Modifier.weight(1f))
                Text("${status.location}${if (status.availableForConversation) " · доступен" else " · занят"}", color = Muted, fontSize = 9.sp)
            }
        }
        Text("Следующее изменение распорядка через ${NpcScheduleManager.hoursUntilNextChange(worldHour)} ч.", color = Muted, fontSize = 9.sp)
        Spacer(Modifier.height(8.dp))
        Text("ОТНОШЕНИЯ С ФРАКЦИЯМИ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val factionSnapshot = FactionManager.snapshot(crownRep, merchantRep, peopleRep, roseRep)
        factionSnapshot.standings.forEach { standing ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(standing.displayName, color = Ink, fontSize = 11.sp, modifier = Modifier.weight(1f))
                Text("${standing.rank} · ${standing.score}", color = Muted, fontSize = 10.sp)
            }
        }
        val politicalAgenda = FactionAgendaManager.evaluate(crownRep, merchantRep, peopleRep, roseRep)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp)) {
                Text("ПОЛИТИЧЕСКАЯ ПОВЕСТКА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (politicalAgenda.isEmpty()) {
                    Text("Фракции пока не выдвигают заметных требований. Сохраняй равновесие — оно тоже имеет цену.", color = Ink, fontSize = 11.sp, lineHeight = 16.sp)
                } else {
                    politicalAgenda.take(3).forEach { agenda ->
                        Spacer(Modifier.height(5.dp))
                        Text("${agenda.title} · ${agenda.source} · ${"!".repeat(agenda.urgency)}", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(agenda.pressure, color = Ink, fontSize = 10.sp, lineHeight = 14.sp)
                        Text("Возможность: ${agenda.opportunity}", color = Muted, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                    if (politicalAgenda.size > 3) Text("И ещё ${politicalAgenda.size - 3} политических вопроса остаются в тени.", color = Muted, fontSize = 9.sp)
                }
            }
        }
        val realmPriorities = RealmPriorityManager.evaluate(
            gold = gold, provisions = provisions, dailyRations = GameRules.dailyArmyRations(royalArmy),
            army = royalArmy, guard = guard, provinceNames = provinceNames,
            provinceLoyalty = provinceLoyalty.toList(), provinceCrisisDays = provinceCrisisDays.toList(),
            crownRep = crownRep, merchantRep = merchantRep, peopleRep = peopleRep, roseRep = roseRep
        )
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp)) {
                Text("ПРИОРИТЕТЫ КОРОЛЕВСКОГО СОВЕТА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (realmPriorities.isEmpty()) {
                    Text("Системы королевства находятся в рабочем равновесии. Совет не выделяет срочных угроз.", color = Ink, fontSize = 11.sp, lineHeight = 16.sp)
                } else {
                    realmPriorities.forEach { priority ->
                        Spacer(Modifier.height(5.dp))
                        Text("${priority.title} · ${"!".repeat(priority.urgency)}", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(priority.reason, color = Ink, fontSize = 10.sp, lineHeight = 14.sp)
                        Text("Совет: ${priority.recommendation}", color = Muted, fontSize = 10.sp, lineHeight = 14.sp)
                    }
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(10.dp)) {
                Text("СЛУЧАЙНОЕ СОБЫТИЕ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Text(if (randomEventCount == 0) "Улицы полны слухов. Осмотрись: иногда небольшое происшествие меняет ход большого расследования." else worldEvent, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
                Button(onClick = {
                    val event = (0..4).random()
                    randomEventCount++
                    when (event) {
                        0 -> { gold = ResourceRules.addGold(gold, 3); worldEvent = "У мостовой ты находишь обронённый кошель. Внутри три золотых и записка с гербом одного из великих домов."; addInventoryItem("Записка с гербом дома"); addJournalEntry("Случайное событие: найден кошель и записка.") }
                        1 -> { health = (health - 5).coerceAtLeast(1); worldEvent = "На узкой улице вспыхивает драка. Ты помогаешь дозорному, но получаешь лёгкую рану. Здоровье −5."; addJournalEntry("Случайное событие: уличная драка, здоровье −5.") }
                        2 -> { peopleRep = (peopleRep + 3).coerceAtMost(100); reputation = (reputation + 1).coerceAtMost(100); worldEvent = "Ремесленники просят помочь донести ящики. Простые люди запоминают твою помощь. Народная поддержка выросла."; addJournalEntry("Случайное событие: помог ремесленникам.") }
                        3 -> { influence++; worldEvent = "Незнакомец в сером плаще передаёт тебе обрывок шифра и исчезает в толпе. Возможно, это след Чёрной розы."; addInventoryItem("Обрывок неизвестного шифра"); addJournalEntry("Случайное событие: получен обрывок шифра.") }
                        else -> { gold = (gold - 2).coerceAtLeast(0); worldEvent = "Карманник пытается вытащить кошелёк. Ты замечаешь его слишком поздно и теряешь два золотых."; addJournalEntry("Случайное событие: карманная кража, −2 золота.") }
                    }
                    advanceWorldTime(1)
                    save()
                }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
                    Text("Осмотреться · новое событие", color = Ink, fontSize = 11.sp)
                }
                Text("Найдено событий: $randomEventCount", color = Muted, fontSize = 10.sp)
            }
        }
        Spacer(Modifier.height(6.dp))
        when (currentLocation) {
            "Королевский замок" -> {
                RelationCard("Капитан Гаррет Морн", "У оружейной башни · память о ваших встречах: $garrettMemory", (guard).coerceIn(0,100), "Доверие")
                Text("«Эдриан, стража устала от слухов. Скажи, что ты ищешь на самом деле». ", color = Ink, fontSize = 12.sp)
                Button(onClick = {
                    if (socialActionFlags and 8 == 0) {
                        socialActionFlags = socialActionFlags or 8; guard = (guard + 3).coerceAtMost(100); garrettMemory++
                        addJournalEntry("Разговор с Гарретом: обсудили безопасность замка."); lastOutcome = "Гаррет запомнил твою прямоту. Доверие гвардии немного выросло."
                    } else lastOutcome = "Вы уже обсуждали безопасность замка. Гаррет ждёт новых обстоятельств, а не повторения прежнего разговора."
                    save()
                }, enabled = socialActionFlags and 8 == 0, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (socialActionFlags and 8 == 0) "Поговорить о безопасности · первый разговор" else "Безопасность замка уже обсуждали", color = Paper) }
                Button(onClick = {
                    if (socialActionFlags and 64 != 0) {
                        lastOutcome = "Ты уже передал Гаррету накладную. Он продолжает проверку, но повторная передача не принесёт новых наград."
                    } else if (hasInventoryItem("Копия портовой накладной")) {
                        removeAllInventoryItems("Копия портовой накладной")
                        socialActionFlags = socialActionFlags or 64
                        guard = (guard + 7).coerceAtMost(100)
                        reputation = (reputation + 1).coerceAtMost(100)
                        addJournalEntry("Передал Гаррету копию портовой накладной; улика изъята для расследования.")
                        lastOutcome = "Гаррет забирает накладную как доказательство и обещает проверить ночные поставки. Доверие гвардии +7, репутация +1."
                    } else {
                        lastOutcome = "Гаррет просит принести доказательство ночных поставок из порта."
                    }
                    save()
                }, enabled = socialActionFlags and 64 == 0, colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                    Text(if (socialActionFlags and 64 == 0) "Передать накладную Гаррету" else "Накладная передана на расследование", color = Paper)
                }
            }
            "Зимний сад" -> {
                RelationCard("Леди Селеста", "У фонтана · память о разговорах: $selesteMemory", (seleste+50).coerceIn(0,100), "Расположение")
                Text("«Слова — это тоже обещания. Я помню не только то, что ты сказал, но и то, чего не сказал».", color = Ink, fontSize = 12.sp)
                Button(onClick = {
                    if (socialActionFlags and 16 == 0) {
                        socialActionFlags = socialActionFlags or 16; seleste = (seleste + 4).coerceIn(-100, 100); selesteMemory++
                        if (!hasInventoryItem("Слух о корабле без печати")) addInventoryItem("Слух о корабле без печати")
                        addJournalEntry("Тайный разговор с Селестой в зимнем саду."); lastOutcome = "Селеста поделилась слухом: один из кораблей покинул порт без королевской печати."
                    } else lastOutcome = "Селеста уже рассказала тебе о корабле без печати. Повторный разговор не приносит новых сведений."
                    save()
                }, enabled = socialActionFlags and 16 == 0, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (socialActionFlags and 16 == 0) "Выслушать её тайну" else "Тайна Селесты уже раскрыта", color = Paper) }
                Button(onClick = { if (gold>=2) { gold-=2; seleste=(seleste+2).coerceIn(-100,100); lastOutcome="Ты купил редкий чай для Селесты. Она оценила внимание."; addJournalEntry("Подарил Селесте редкий чай.") } else lastOutcome="Не хватает золота на подарок (2 золотых)."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Купить редкий чай · 2 золота", color = Paper) }
            }
            "Нижний город" -> {
                RelationCard("Мира — торговка специями", "Рынок · знает городские слухи", (merchantRep+50).coerceIn(0,100), "Доверие")
                Text("«У меня есть товары и новости. Но бесплатных историй на рынке не бывает».", color = Ink, fontSize = 12.sp)
                Text("ТОРГОВАЯ ЛАВКА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Button(onClick = { if (gold>=4) { if (addInventoryItem("Лечебный бальзам")) { gold-=4; addJournalEntry("Куплен лечебный бальзам за 4 золота."); lastOutcome="Бальзам добавлен в инвентарь. Его можно использовать в опасном путешествии." } } else lastOutcome="Недостаточно золота: нужно 4."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Лечебный бальзам · 4 золота", color = Paper) }
                Button(onClick = {
                    if (socialActionFlags and 128 != 0) lastOutcome = "Мира уже продала тебе это письмо. Повторная покупка той же улики невозможна."
                    else if (gold >= 3) {
                        socialActionFlags = socialActionFlags or 128
                        gold -= 3
                        addUniqueQuestItem("Письмо без подписи")
                        merchantRep = (merchantRep + 3).coerceIn(-100, 100)
                        lastOutcome = "Мира продала тебе письмо без подписи. На бумаге пахнет речной солью."
                        addJournalEntry("Куплено письмо без подписи у Миры.")
                    } else lastOutcome = "Недостаточно золота: нужно 3."
                    save()
                }, enabled = socialActionFlags and 128 == 0, colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
                    Text(if (socialActionFlags and 128 == 0) "Письмо без подписи · 3 золота" else "Письмо уже куплено", color = Paper)
                }
                Button(onClick = { if (gold>=12 && !hasInventoryItem("Меч стражника")) { if (addInventoryItem("Меч стражника")) { gold-=12; lastOutcome="Мира помогла достать меч стражника. Твои атаки стали сильнее."; addJournalEntry("Куплен меч стражника за 12 золотых.") } } else lastOutcome=if (hasInventoryItem("Меч стражника")) "Меч уже есть." else "Недостаточно золота: нужно 12."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Меч стражника · 12 золота", color = Ink) }
                Button(onClick = { if (gold>=15 && !hasInventoryItem("Кольчуга стражника")) { if (addInventoryItem("Кольчуга стражника")) { gold-=15; lastOutcome="Ты приобрёл кольчугу. В бою она уменьшает входящий урон."; addJournalEntry("Куплена кольчуга стражника за 15 золотых.") } } else lastOutcome=if (hasInventoryItem("Кольчуга стражника")) "Кольчуга уже есть." else "Недостаточно золота: нужно 15."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Кольчуга стражника · 15 золота", color = Ink) }
                Button(onClick = {
                    val action = when {
                        questStage == 0 -> MedicineQuestAction.ACCEPT
                        questStage == 1 && questMedicineCrateFound -> MedicineQuestAction.COMPLETE
                        else -> MedicineQuestAction.COMPLETE
                    }
                    applyMedicineQuestTransition(QuestManager.transitionMedicineQuest(MedicineQuestState(questStage, questMedicineCrateFound), action))
                }, colors = ButtonDefaults.buttonColors(containerColor = Gold), enabled = questStage<2) { Text(if (questStage==0) "Взять задание: пропавшие лекарства" else if (questMedicineCrateFound) "Сообщить о выполнении задания" else "Ящик ещё не найден", color = Ink) }
            }
            "Порт Веймар" -> {
                RelationCard("Томас — грузчик причала", "Смены по ночам · осторожен с чужаками", (peopleRep+50).coerceIn(0,100), "Расположение")
                Text("«Я ничего не видел. А если видел — мне за молчание платят».", color = Ink, fontSize = 12.sp)
                Button(onClick = {
                    if (socialActionFlags and 32 != 0) lastOutcome = "Копия портовой накладной уже у тебя. Томас не станет продавать одну и ту же улику повторно."
                    else if (gold >= 2) {
                        socialActionFlags = socialActionFlags or 32; gold -= 2; merchantRep = (merchantRep + 2).coerceIn(-100, 100)
                        if (!hasInventoryItem("Копия портовой накладной")) addInventoryItem("Копия портовой накладной")
                        addJournalEntry("Подкупил грузчика и получил копию портовой накладной."); lastOutcome = "Томас передал копию накладной. Среди грузов отмечен ящик с королевской печатью."
                    } else lastOutcome = "Томас требует 2 золотых за сведения."
                    save()
                }, enabled = socialActionFlags and 32 == 0, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (socialActionFlags and 32 == 0) "Заплатить за сведения · 2 золота" else "Накладная уже получена", color = Paper) }
                Button(onClick = {
                    if (questStage == 1) {
                        applyMedicineQuestTransition(QuestManager.transitionMedicineQuest(MedicineQuestState(questStage, questMedicineCrateFound), MedicineQuestAction.FIND_CRATE))
                    } else {
                        randomEventCount++
                        val event = if (randomEventCount % 2 == 0) "На мокрых досках ты находишь обрывок синей ткани. Он пахнет дымом и дорогими духами." else "Матрос шепчет о судне, которое каждую неделю уходит без огней. Его капитан носит перстень с чёрной розой."
                        val clue = if (randomEventCount % 2 == 0) "Обрывок синей ткани" else "Слух о ночном судне"
                        addUniqueQuestItem(clue)
                        lastOutcome = event
                        addJournalEntry("Случайная находка в порту: $event")
                        save()
                    }
                }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (questStage==1) "Проверить ящик с лекарствами" else "Осмотреть причалы", color = Paper) }
            }
            else -> {
                Text("У старого моста слышно, как вода бьётся о камни. Здесь можно искать следы и собирать улики.", color = Ink, fontSize = 13.sp)
                val bridgeInvestigationDone = (worldInvestigationFlags and 8) != 0
                Button(onClick = {
                    if (!bridgeInvestigationDone) {
                        randomEventCount++; val clue = if (randomEventCount % 2 == 0) "Следы сапог с гербовой пряжкой" else "Сломанная печать из чёрного воска"
                        if (!hasInventoryItem(clue)) addInventoryItem(clue)
                        influence = (influence + 1).coerceAtMost(100); worldInvestigationFlags = worldInvestigationFlags or 8
                        addJournalEntry("Исследование старого моста: найдено — $clue."); lastOutcome = "Ты обнаружил: $clue. Улика добавлена в инвентарь."
                    } else lastOutcome = "Ты уже тщательно осмотрел старый мост. Новых улик здесь не осталось."
                    save()
                }, enabled = !bridgeInvestigationDone, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (bridgeInvestigationDone) "Старый мост уже исследован" else "Тщательно осмотреть мост", color = Paper) }
            }
        }
        Spacer(Modifier.height(10.dp))
        Spacer(Modifier.height(12.dp))
        Text("ЛИЧНЫЕ ИСТОРИИ · ЛЮДИ ПОМНЯТ ТВОИ ПОСТУПКИ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("У каждого знакомого есть собственные заботы. Повторные беседы открывают новые подробности; выполненные просьбы отмечаются в летописи.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        Card(colors = CardDefaults.cardColors(containerColor = Color(0xFFE5D5B8)), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(9.dp)) {
                Text("ГАРРЕТ · ДОЛГ СТАРОГО СОЛДАТА", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Память: $garrettMemory бесед · доверие гвардии: $guard", color = Muted, fontSize = 10.sp)
                Button(onClick = { garrettMemory++; if (garrettMemory >= 3 && personalQuestFlags and 1 == 0) { if (hasInventoryItem("Записка старого ветерана") || addUniqueQuestItem("Записка старого ветерана")) { personalQuestFlags = personalQuestFlags or 1; guard=(guard+5).coerceAtMost(100); lastOutcome="Гаррет доверяет тебе историю о ветеране, исчезнувшем после приказа с поддельной печатью. Ты получил записку с номером караульного журнала."; addJournalEntry("Личная история Гаррета: получена записка ветерана.") } else lastOutcome="Сумка заполнена. Освободи место, чтобы принять записку Гаррета." } else { lastOutcome=if (personalQuestFlags and 1 != 0) "Гаррет уже рассказал тебе о старом ветеране. Теперь он ждёт результатов расследования." else "Гаррет вспоминает службу при Альдрике. Он станет откровеннее после нескольких честных разговоров." }; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Поговорить с Гарретом", color = Paper) }
                HorizontalDivider(color = Color(0xFFB49A70))
                Text("СЕЛЕСТА · ЦЕНА МОЛЧАНИЯ", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Память: $selesteMemory бесед · расположение: $seleste", color = Muted, fontSize = 10.sp)
                Button(onClick = { selesteMemory++; if (seleste >= 12 && personalQuestFlags and 2 == 0) { if (hasInventoryItem("Половина шифрованного письма") || addUniqueQuestItem("Половина шифрованного письма")) { personalQuestFlags = personalQuestFlags or 2; influence = ResourceRules.addInfluence(influence, 2); lastOutcome="Селеста признаётся, что её семья укрывала посланника исчезнувшего наследника. Она отдаёт половину письма; вторую хранит человек в порту."; addJournalEntry("Личная история Селесты: получена половина шифрованного письма.") } else lastOutcome="Сумка заполнена. Освободи место, чтобы принять половину письма Селесты." } else { lastOutcome=if (personalQuestFlags and 2 != 0) "Селеста уже поделилась семейной тайной. Её доверие теперь зависит от того, как ты распорядился письмом." else "Селеста говорит уклончиво. Ей нужно больше оснований доверять тебе (расположение не ниже 12)." }; save() }, enabled = personalQuestFlags and 2 == 0 || selesteMemory < 99, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Выслушать Селесту", color = Paper) }
                HorizontalDivider(color = Color(0xFFB49A70))
                Text("МИРА · ДОЛГ РЫНКА", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Память: $miraMemory бесед · доверие купцов: $merchantRep", color = Muted, fontSize = 10.sp)
                Button(onClick = { miraMemory++; if (personalQuestFlags and 4 == 0 && questStage >= 2) { if (hasInventoryItem("Медная торговая метка") || addUniqueQuestItem("Медная торговая метка")) { personalQuestFlags = personalQuestFlags or 4; gold=(gold+5).coerceAtMost(1_000_000); merchantRep=(merchantRep+4).coerceIn(-100,100); lastOutcome="Мира помнит, что ты вернул лекарства. Она раскрывает тайник с оплатой для честного посредника: 5 золотых и торговая метка."; addJournalEntry("Личная история Миры: заслужена торговая метка.") } else lastOutcome="Сумка заполнена. Освободи место, чтобы получить торговую метку Миры." } else { lastOutcome=if (questStage < 2) "Мира просит сначала доказать, что ты не бросаешь людей в беде. Заверши задание о пропавших лекарствах." else "Мира рассказывает, как пережила голодную зиму и открыла лавку. Она считает тебя надёжным знакомым." }; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Поговорить с Мирой", color = Paper) }
                HorizontalDivider(color = Color(0xFFB49A70))
                Text("ТОМАС · СВИДЕТЕЛЬ НОЧНОГО ПОРТА", color = Ink, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Память: $thomasMemory бесед · народная поддержка: $peopleRep", color = Muted, fontSize = 10.sp)
                Button(onClick = { thomasMemory++; if (worldHour in 20..23 || worldHour in 0..4) { if (personalQuestFlags and 8 == 0) { if (hasInventoryItem("Список ночных грузов") || addUniqueQuestItem("Список ночных грузов")) { personalQuestFlags = personalQuestFlags or 8; peopleRep=(peopleRep+2).coerceIn(-100,100); lastOutcome="Ночью Томас решается показать список грузов. Один ящик отмечен знаком Чёрной розы и отправлен в старую часовню."; addJournalEntry("Личная история Томаса: получен список ночных грузов.") } else lastOutcome="Сумка заполнена. Освободи место, чтобы принять список Томаса." } else lastOutcome="Томас больше не скрывает список. Он просит не называть его имя." } else lastOutcome="Томас не станет говорить при дневной смене. Попробуй вернуться в порт ночью."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Расспросить Томаса ночью", color = Paper) }
            }
        }
        Text("ЛИЧНЫЕ ИСТОРИИ: ${Integer.bitCount(personalQuestFlags)} / 4 завершено", color = Muted, fontSize = 10.sp)
        Text("ЗАДАНИЕ: ПРОПАВШИЕ ЛЕКАРСТВА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text(when(questStage) { 0 -> "Мира на рынке ещё не просила о помощи."; 1 -> "Найти ящик с лекарствами в порту Веймара."; else -> "Выполнено. Горожане запомнят твою помощь." }, color = Ink, fontSize = 12.sp)
        Text("СЛУЧАЙНЫЕ СОБЫТИЯ: $randomEventCount", color = Muted, fontSize = 10.sp)
    }
    @Composable
    fun RenderPage_quests() {
        PaperHeading("ЖУРНАЛ ЗАДАНИЙ")
        Text("ГЛАВА I · ЧЁРНАЯ РОЗА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        val questTitles = listOf("Неизвестный груз", "След ведёт к часовне", "Письмо без подписи", "Чья сторона?", "Последствия выбора", "Цепочка завершена")
        Text("Этап ${roseQuestStage.coerceAtMost(5)} / 5 · ${questTitles[roseQuestStage]}", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(when (roseQuestStage) {
            0 -> "Томас упоминал груз со знаком Чёрной розы. Найди его след в порту ночью. Нужен список ночных грузов или слух о ночном судне."
            1 -> "В списке Томаса указан груз для старой часовни. Отправляйся туда и осмотри следы."
            2 -> "В тайнике часовни найдено письмо. Теперь реши, кому доверить улику: Селесте или Гаррету."
            3 -> "Союзник ждёт твоего решения. Можно передать письмо королевской страже или скрыть его и продолжить тайное расследование."
            4 -> "Твой выбор повлиял на политические силы. Вернись к союзнику, чтобы завершить эту главу."
            else -> "Ты завершил цепочку расследования. Последствия останутся в летописи и отношениях фракций."
        }, color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(8.dp))
        when (roseQuestStage) {
            0 -> Button(onClick = { if (QuestManager.canAdvanceBlackRoseFromEvidence(roseQuestStage, hasInventoryItem("Список ночных грузов"), hasInventoryItem("Слух о ночном судне"))) { roseQuestStage = 1; addJournalEntry("Цепочка Чёрной розы: найден след ночного груза."); lastOutcome = "След подтверждён. Груз направляли к старой часовне." } else lastOutcome = "Пока недостаточно улик. Поговори с Томасом ночью или осмотри причалы."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Проверить улики", color = Ink) }
            1 -> Button(onClick = { if (QuestManager.canSearchOldChapel(roseQuestStage, currentLocation)) { if (hasInventoryItem("Запечатанное письмо Чёрной розы") || addUniqueQuestItem("Запечатанное письмо Чёрной розы")) { roseQuestStage = 2; addJournalEntry("В старой часовне найдено запечатанное письмо."); lastOutcome = "Под каменной плитой ты нашёл письмо с печатью Чёрной розы." } else { lastOutcome = "Сумка заполнена. Освободи место, чтобы забрать запечатанное письмо." } } else lastOutcome = "Сначала отправляйся в локацию «Старая часовня» на карте."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Осмотреть часовню", color = Ink) }
            2 -> { Text("Выбери, кому доверить письмо:", color = Ink); Button(onClick = { roseQuestPath = 1; roseQuestStage = 3; seleste = (seleste+5).coerceIn(-100,100); influence = ResourceRules.addInfluence(influence, 2); addJournalEntry("Письмо доверено Селесте. Она обещала расшифровать его."); lastOutcome = "Селеста принимает письмо и обещает расшифровать печать."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Доверить Селесте", color = Paper) }; Button(onClick = { roseQuestPath = 2; roseQuestStage = 3; guard = (guard+5).coerceAtMost(100); crownRep = (crownRep+4).coerceIn(-100,100); addJournalEntry("Письмо передано Гаррету для проверки королевской печати."); lastOutcome = "Гаррет узнаёт во фрагменте знак старого караула и просит действовать осторожно."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Доверить Гаррету", color = Paper) } }
            3 -> { Button(onClick = { roseQuestStage = 4; if (roseQuestPath == 1) { seleste = (seleste+3).coerceIn(-100,100); merchantRep = (merchantRep-1).coerceIn(-100,100); lastOutcome = "Селеста расшифровала часть письма: наследника видели живым после коронации." } else { crownRep = (crownRep+3).coerceIn(-100,100); roseRep = (roseRep+2).coerceIn(-100,100); lastOutcome = "Гаррет подтвердил подделку приказа. Королевская стража теперь насторожена." }; addJournalEntry(lastOutcome ?: "Решение принято."); save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text(if (roseQuestPath == 1) "Продолжить тайное расследование" else "Передать сведения страже", color = Ink) } }
            4 -> Button(onClick = { if (hasInventoryItem("Печать старой часовни") || addUniqueQuestItem("Печать старой часовни")) { roseQuestStage = 5; gold = (gold + 12).coerceAtMost(1_000_000); grantExperience(25); if (roseQuestPath == 1) { roseRep = (roseRep+5).coerceIn(-100,100) } else { crownRep = (crownRep+3).coerceIn(-100,100) }; addJournalEntry("Цепочка «Чёрная роза» завершена. Получено 12 золотых и 25 опыта."); lastOutcome = "Глава расследования завершена. Получено 12 золотых, 25 опыта и печать часовни." } else { lastOutcome = "Сумка заполнена. Освободи место, чтобы получить печать старой часовни." }; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Завершить расследование", color = Ink) }
            else -> Text("Награды получены: 12 золота · 25 опыта · Печать старой часовни", color = Ink, fontSize = 12.sp)
        }
        Spacer(Modifier.height(14.dp)); HorizontalDivider(color = Color(0xFF8B6B45))
        Text("ГЛАВА II · СОВЕТ СЕМИ ДОМОВ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("${when (housesQuestStage) { 0 -> "Закрытое заседание"; 1 -> "Выбор союзника"; 2 -> "Последствия союза"; else -> "Голоса короны" }} · ${housesQuestStage}/3", color = Ink, fontWeight = FontWeight.Bold, fontSize = 15.sp)
        Text(when (housesQuestStage) {
            0 -> "После находки в часовне герцогские посланники собираются на закрытый совет. Чтобы войти в большую политику, предъяви Печать старой часовни."
            1 -> "Три силы готовы поддержать расследование, но каждая потребует свою цену. Торговцы хотят свободы пошлин; горожане — хлеба и защиты; сторонники Чёрной розы — тайны и осторожности."
            2 -> "Союзник ждёт, когда ты докажешь, что выбор был не пустым обещанием. Последствия отразятся на влиянии домов."
            else -> "Ты изменил расстановку сил перед Советом. Великие дома теперь считают Эдриана самостоятельным игроком."
        }, color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        when (housesQuestStage) {
            0 -> Button(onClick = { if (QuestManager.canEnterSevenHousesCouncil(roseQuestStage, hasInventoryItem("Печать старой часовни"))) { housesQuestStage = 1; addJournalEntry("Открыта глава II: приглашение на Совет семи домов."); lastOutcome = "Печать часовни открыла тебе двери закрытого совета. Три посланника ждут ответа." } else lastOutcome = "Сначала заверши расследование «Чёрная роза» и получи печать старой часовни."; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Предъявить печать и войти", color = Ink) }
            1 -> {
                Text("Кому предложить союз?", color = Ink, fontWeight = FontWeight.Bold)
                Button(onClick = { if (QuestManager.canChooseCouncilAlliance(housesQuestStage)) { housesQuestPath = 1; housesQuestStage = 2; merchantRep = (merchantRep+8).coerceIn(-100,100); influence = ResourceRules.addInfluence(influence, 2); addJournalEntry("Совет семи домов: заключён торговый союз."); lastOutcome = "Посланник гильдий принимает предложение. Он обещает открыть склады, но требует пересмотра королевских пошлин."; save() } }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Гильдии торговцев — ресурсы и золото", color = Paper) }
                Button(onClick = { if (QuestManager.canChooseCouncilAlliance(housesQuestStage)) { housesQuestPath = 2; housesQuestStage = 2; peopleRep = (peopleRep+8).coerceIn(-100,100); guard = (guard+3).coerceAtMost(100); addJournalEntry("Совет семи домов: заключён союз с горожанами."); lastOutcome = "Представитель горожан поддерживает тебя. Народ ждёт защиты от произвола знати."; save() } }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Горожане — поддержка и доверие", color = Paper) }
                Button(onClick = { if (QuestManager.canChooseCouncilAlliance(housesQuestStage)) { housesQuestPath = 3; housesQuestStage = 2; roseRep = (roseRep+8).coerceIn(-100,100); influence = ResourceRules.addInfluence(influence, 4); addJournalEntry("Совет семи домов: заключён тайный союз."); lastOutcome = "Безымянный посланник Чёрной розы предлагает сведения, но теперь твои шаги будут скрыты от большинства."; save() } }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Тайный круг — влияние и секреты", color = Paper) }
            }
            2 -> Button(onClick = { if (QuestManager.canFinalizeCouncilAlliance(housesQuestStage, housesQuestPath)) { if (hasInventoryItem("Знак Совета семи домов") || addUniqueQuestItem("Знак Совета семи домов")) { housesQuestStage = 3; gold = (gold + if (housesQuestPath == 1) 15 else 5).coerceAtMost(1_000_000); grantExperience(35); influence = ResourceRules.addInfluence(influence, 2); addJournalEntry("Глава II завершена. Выбранный союз изменил баланс сил."); lastOutcome = "Ты закрепил союз на Совете. Получено 35 опыта, знак Совета и награда золотом. Твой выбор будет иметь последствия в следующих главах." } else { lastOutcome = "Сумка заполнена. Освободи место, чтобы получить знак Совета семи домов." } }; save() }, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Закрепить союз", color = Ink) }
            else -> Text("Союз: ${when(housesQuestPath) { 1 -> "Гильдии торговцев"; 2 -> "Горожане"; 3 -> "Тайный круг"; else -> "не выбран" }} · Награды получены", color = Ink, fontSize = 12.sp)
        }
        Spacer(Modifier.height(12.dp)); HorizontalDivider(color = Color(0xFFB49A70))
        Text("ДОПОЛНИТЕЛЬНЫЕ ЗАДАНИЯ", color = Muted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text("Пропавшие лекарства: ${if (questStage >= 2) "выполнено" else if (questStage == 1) "активно — ищи в порту" else "ещё не принято"}", color = Ink, fontSize = 12.sp)
        Text("Личные истории: ${Integer.bitCount(personalQuestFlags)} / 4", color = Ink, fontSize = 12.sp)
        Text("Решения в цепочке: ${when (roseQuestPath) { 1 -> "Селеста"; 2 -> "Гаррет"; else -> "ещё не выбрано" }}", color = Muted, fontSize = 11.sp)
    }
    @Composable
    fun RenderPage_expeditions() {
        PaperHeading("ЗАБЫТЫЕ МЕСТА · ЭКСПЕДИЦИИ")
        Text("За пределами привычных дорог сохранились убежища старого королевства. Каждая вылазка расходует время; найденные улики и трофеи остаются с тобой.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(8.dp))
        val weather = when ((worldDay + worldHour / 6 + weatherSeed) % 5) { 0 -> "Дождь над Эстервaлем"; 1 -> "Холодный туман"; 2 -> "Ясное небо"; 3 -> "Сильный ветер"; else -> "Мелкий снег в северных землях" }
        Text("ПОГОДА · $weather", color = Muted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text("День $worldDay · ${worldHour.toString().padStart(2,'0')}:00 · $timePhase", color = Ink, fontSize = 12.sp)
        Spacer(Modifier.height(8.dp)); HorizontalDivider(color = Color(0xFFB49A70))
        Text("I · ПОДЗЕМЕЛЬЕ ЗАМКА", color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("Старые камеры под крепостью. Здесь могли хранить документы до основания нынешней династии. Прогресс: $dungeonKeep/4", color = Ink, fontSize = 12.sp)
        Button(onClick = {
            if (dungeonKeep < 4) {
                advanceWorldTime(1); dungeonKeep++
                when (dungeonKeep) {
                    1 -> { addUniqueQuestItem("Ржавый ключ подземелья"); expeditionNote = "В пыли найден ключ с гербом прежней династии."; gold = ResourceRules.addGold(gold, 2) }
                    2 -> { health = (health - 8).coerceAtLeast(1); addInventoryItem("Обломок старой печати"); expeditionNote = "Каменная плита сдвинулась и задела тебя. Под ней лежал обломок печати." }
                    3 -> { addUniqueQuestItem("Записи тайного караула"); influence = ResourceRules.addInfluence(influence, 3); expeditionNote = "Ты обнаружил записи караула: приказы поступали из покоев регента." }
                    4 -> { addInventoryItem("Королевский медальон"); grantExperience(60); expeditionNote = "В последней камере найден медальон пропавшего наследника. Это важнейшая улика." }
                }
                addJournalEntry("Экспедиция: подземелье замка, этап $dungeonKeep. $expeditionNote"); save()
            } else expeditionNote = "Подземелье замка исследовано полностью. Здесь больше нечего искать."
        }, enabled = dungeonKeep < 4, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (dungeonKeep < 4) "Исследовать следующую камеру · 1 час" else "Исследовано", color = Paper) }
        Spacer(Modifier.height(9.dp)); HorizontalDivider(color = Color(0xFFB49A70))
        Text("II · СКЛЕП СТАРОЙ ЧАСОВНИ", color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("Запечатанный склеп открывается после продвижения расследования Чёрной розы. Прогресс: $dungeonCrypt/4", color = Ink, fontSize = 12.sp)
        Button(onClick = {
            if (roseQuestStage >= 1 && dungeonCrypt < 4) {
                advanceWorldTime(2); dungeonCrypt++
                when (dungeonCrypt) {
                    1 -> { addInventoryItem("Свеча погребального обряда"); expeditionNote = "В нише сохранилась ритуальная свеча." }
                    2 -> { if (health > 20) health -= 12 else gold = (gold - 4).coerceAtLeast(0); addInventoryItem("Кость с руной"); expeditionNote = "Ловушка сработала: рунический шип ранил тебя. В нише найдена кость с руной." }
                    3 -> { addInventoryItem("Реестр тайных погребений"); roseRep = FactionManager.applyDelta(roseRep, 4); expeditionNote = "Реестр доказывает, что некоторые люди официально умерли, но продолжали жить." }
                    4 -> { addInventoryItem("Перстень смотрителя склепа"); grantExperience(75); expeditionNote = "В саркофаге найден перстень смотрителя — знак доступа к тайным криптам." }
                }
                addJournalEntry("Экспедиция: склеп часовни, этап $dungeonCrypt. $expeditionNote"); save()
            } else if (roseQuestStage < 1) expeditionNote = "Склеп закрыт. Сначала найди след Чёрной розы."
            else expeditionNote = "Склеп полностью исследован."
        }, enabled = roseQuestStage >= 1 && dungeonCrypt < 4, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (roseQuestStage < 1) "Закрыто · нужен след Чёрной розы" else if (dungeonCrypt < 4) "Спуститься в склеп · 2 часа" else "Исследовано", color = Paper) }
        Spacer(Modifier.height(9.dp)); HorizontalDivider(color = Color(0xFFB49A70))
        Text("III · ЗАБЫТЫЙ АРХИВ", color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("Тайное хранилище летописей, доступное тем, кто собрал печати и заслужил влияние. Прогресс: $dungeonArchive/4", color = Ink, fontSize = 12.sp)
        Button(onClick = {
            if (influence >= 5 && dungeonArchive < 4) {
                advanceWorldTime(2); dungeonArchive++
                when (dungeonArchive) {
                    1 -> { addInventoryItem("План старого архива"); expeditionNote = "План показывает скрытый проход за книжными шкафами." }
                    2 -> { addUniqueQuestItem("Серебряная закладка"); gold = ResourceRules.addGold(gold, 8); expeditionNote = "За фальшивой стеной спрятана серебряная закладка и золото." }
                    3 -> { addUniqueQuestItem("Черновик завещания Альдрика III"); influence = ResourceRules.addInfluence(influence, 5); expeditionNote = "Черновик завещания указывает на подмену наследника." }
                    4 -> { addInventoryItem("Письмо истинного наследника"); grantExperience(100); expeditionNote = "Ты нашёл письмо наследника. Оно меняет смысл всей истории." }
                }
                addJournalEntry("Экспедиция: забытый архив, этап $dungeonArchive. $expeditionNote"); save()
            } else if (influence < 5) expeditionNote = "Нужны влияние не менее 5 и доступ к старым архивным печатям."
            else expeditionNote = "Архив полностью исследован."
        }, enabled = influence >= 5 && dungeonArchive < 4, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (influence < 5) "Закрыто · требуется влияние 5" else if (dungeonArchive < 4) "Изучить архив · 2 часа" else "Исследовано", color = Paper) }
        Spacer(Modifier.height(10.dp)); HorizontalDivider(color = Color(0xFFB49A70))
        Text("ПОСЛЕДНЯЯ НАХОДКА", color = Muted, fontWeight = FontWeight.Bold, fontSize = 10.sp)
        Text(expeditionNote, color = Ink, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(8.dp))
        Text("ПОХОДНОЕ СНАРЯЖЕНИЕ И РЕМЕСЛО", color = Muted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        Text("Собирай травы и металл в мире, затем готовь припасы. Рецепт бальзама: 2 лечебные травы + 2 золота. Рецепт усиленной брони: 3 железных обломка + 8 золота.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        Button(onClick = { advanceWorldTime(2); if ((1..100).random() <= 65) { addInventoryItem("Лечебная трава"); expeditionNote = "Ты собрал лечебную траву на обочине. Её можно использовать для приготовления бальзама." } else { addInventoryItem("Железный обломок"); expeditionNote = "Среди старого мусора найден железный обломок для кузнечного дела." }; addJournalEntry("Сбор припасов в окрестностях: $expeditionNote"); save() }, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Собрать припасы в окрестностях · 2 часа", color = Paper) }
        Button(onClick = { if (inventoryItemCount("Лечебная трава") >= 2 && gold >= 2) { val craft = InventoryManager.craft(inventory.toList(), mapOf("Лечебная трава" to 2), "Лечебный бальзам"); if (craft.changed) { inventory.clear(); inventory.addAll(craft.items); gold -= 2; craftedCount++; addJournalEntry("Ремесло: изготовлен лечебный бальзам."); expeditionNote = "Готово: лечебный бальзам добавлен в сумку." } else expeditionNote = craft.message } else expeditionNote = "Не хватает двух лечебных трав или двух золотых."; save() }, enabled = inventory.count { it == "Лечебная трава" } >= 2 && gold >= 2, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Изготовить лечебный бальзам", color = Ink) }
        Button(onClick = { if (inventoryItemCount("Железный обломок") >= 3 && gold >= 8) { val craft = InventoryManager.craft(inventory.toList(), mapOf("Железный обломок" to 3), "Усиленная кольчуга"); if (craft.changed) { inventory.clear(); inventory.addAll(craft.items); gold -= 8; craftedCount++; addJournalEntry("Ремесло: изготовлена усиленная кольчуга."); expeditionNote = "Кольчуга готова: её можно считать постоянным улучшением защиты." } else expeditionNote = craft.message } else expeditionNote = "Не хватает трёх железных обломков или восьми золотых."; save() }, enabled = inventory.count { it == "Железный обломок" } >= 3 && gold >= 8, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Сковать усиленную кольчугу", color = Paper) }
        Text("Создано предметов: $craftedCount · Экспедиции: ${dungeonKeep + dungeonCrypt + dungeonArchive}/12", color = Muted, fontSize = 10.sp)
    }
    @Composable
    fun RenderPage_more() {
        PaperHeading("РАЗДЕЛЫ ХРОНИК")
        Text("Главная книга оставляет место истории. Управление королевством, персонажем и дополнительными занятиями собрано в одном спокойном разделе.", color = Ink, fontSize = 12.sp, lineHeight = 16.sp)
        Spacer(Modifier.height(12.dp))
        listOf("Карта королевства" to "realm_map", "Провинции и наместники" to "provinces", "Королевская казна" to "economy", "Армия и гарнизон" to "army", "Диалоги" to "dialogues", "Дома и фракции" to "houses", "Жизнь города" to "life", "Задания" to "quests", "Экспедиции" to "expeditions", "Совет семи домов" to "council", "Спутники" to "companions", "Северный поход" to "frontier", "Об игре и авторе" to "about").forEach { (label, key) ->
            Button(onClick = { selectedPage = key }, modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather), shape = RoundedCornerShape(4.dp)) { Text(label, color = Paper, fontSize = 13.sp) }
        }
    }
    @Composable
    fun RenderPage_about() {
        PaperHeading("АВТОРСКАЯ ЛЕТОПИСЬ ПРОЕКТА")
        Text("ПЕПЕЛ КОРОНЫ", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
        Text("ASHES OF THE CROWN · Версия 6.0.0-alpha27", color = Muted, fontSize = 12.sp)
        Spacer(Modifier.height(12.dp))
        Text("ОБ АВТОРЕ", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Aziz Karabaev", color = Ink, fontSize = 21.sp, fontWeight = FontWeight.Bold)
        Text("Автор идеи и создатель проекта", color = Ink, fontSize = 13.sp)
        Spacer(Modifier.height(12.dp))
        Text("ДАТА СОЗДАНИЯ", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("18 ноября 2026 года", color = Ink, fontSize = 15.sp)
        Spacer(Modifier.height(12.dp))
        Text("СУТЬ ИГРЫ", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Средневековая ролевая история о борьбе за власть в королевстве Эстерваль. Игрок становится Эдрианом Вейлом — хранителем королевских архивов — и расследует смерть короля, исчезновение наследника и тайну чёрной розы. Решения меняют отношения, репутацию, союзы и ход истории.", color = Ink, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("ЗАМЫСЕЛ ПРОЕКТА", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Создать большую одиночную RPG с нелинейным сюжетом, запоминающими поступки NPC, политическими интригами, исследованием мира, торговлей и несколькими концовками. Игра рассчитана на автономную работу без постоянного интернета.", color = Ink, fontSize = 14.sp, lineHeight = 20.sp)
        Spacer(Modifier.height(12.dp))
        Text("РАЗРАБОТКА", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
        Text("Android · Kotlin · Jetpack Compose\nБазовая версия исходников: Alpha 27. Проект объединяет сюжетные, экономические, боевые и управленческие системы. Сохранения используют схему версии 11 и принимают поддерживаемые предыдущие форматы. Игра рассчитана на автономную работу: музыка и игровые ресурсы находятся внутри приложения. Сейчас продолжается поэтапное разделение большого игрового экрана на независимые модули; итоговая Android-сборка и проверка на устройстве запланированы после завершения всех трёх этапов разработки.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
        Spacer(Modifier.height(10.dp))
        Text("© 2026 Aziz Karabaev · Пепел короны", color = Muted, fontSize = 11.sp)
    }
    @Composable
    fun RenderPage_fallback() {
        val storyBodySize = StoryLayoutRules.bodyFontSp(
            screenHeightDp = screenHeightDp,
            textLength = scene.text.length,
            choiceCount = availableChoices.size,
            ultraCompact = ultraCompact,
            compactLandscape = compactLandscape
        )
        val storyLineSize = storyBodySize + if (ultraCompact) 0.9f else 1.6f
        val portraitWidth = StoryLayoutRules.portraitWidthDp(ultraCompact, compactLandscape)
        val portraitHeight = StoryLayoutRules.portraitHeightDp(ultraCompact, compactLandscape)
        PaperHeading(scene.chapter)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(if (ultraCompact) 4.dp else 7.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(id = R.drawable.portrait_adrian), contentDescription = "Эдриан Вейл", modifier = Modifier.width(portraitWidth.dp).height(portraitHeight.dp).background(Color(0xFF3A2416), RoundedCornerShape(3.dp)).border(2.dp, Color(0xFF5C3D24), RoundedCornerShape(3.dp)).padding(2.dp), contentScale = ContentScale.Crop)
            Column(Modifier.weight(1f)) {
                Text(scene.title, color = Ink, fontSize = if (ultraCompact) 12.sp else if (compactLandscape) 15.sp else 17.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Cursive, lineHeight = if (ultraCompact) 14.sp else 19.sp)
                Text(scene.place, color = Muted, fontSize = if (ultraCompact) 7.sp else 9.sp, fontFamily = FontFamily.Cursive, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        Spacer(Modifier.height(if (ultraCompact) 3.dp else 5.dp))
        Text(scene.text, color = Ink, fontSize = storyBodySize.sp, fontFamily = FontFamily.Serif, lineHeight = storyLineSize.sp)
        Spacer(Modifier.height(if (ultraCompact) 3.dp else 5.dp))
        Box(Modifier.fillMaxWidth().height(1.dp).background(Brush.horizontalGradient(listOf(Color.Transparent, Color(0xFF8D693D), Color.Transparent))))
        // Последствия остаются в журнале/состоянии кампании, но не занимают место на странице чтения.
        Spacer(Modifier.height(if (ultraCompact) 3.dp else 5.dp))
        availableChoices.forEachIndexed { i, choice ->
            Row(
                Modifier.fillMaxWidth().padding(vertical = 1.dp)
                    .background(if (i == 0) Color(0xFFE4C58F) else Color(0xFFE8D7B5), RoundedCornerShape(3.dp))
                    .border(1.dp, Color(0xFF967344), RoundedCornerShape(3.dp))
                    .clickable { makeChoice(choice) }
                    .padding(horizontal = 6.dp, vertical = if (ultraCompact) 2.dp else 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(listOf("✥", "⚖", "♧", "⚔")[i % 4], color = Color(0xFF6F4B26), fontSize = if (ultraCompact) 11.sp else 15.sp)
                Column(Modifier.weight(1f)) {
                    Text(choice.title, color = Ink, fontSize = StoryLayoutRules.choiceTitleFontSp(ultraCompact, compactLandscape).sp, fontWeight = FontWeight.SemiBold, lineHeight = if (ultraCompact) 9.sp else 11.sp)
                    Text(choice.detail, color = Muted, fontSize = StoryLayoutRules.choiceDetailFontSp(ultraCompact, compactLandscape).sp, lineHeight = if (ultraCompact) 8.sp else 9.5.sp)
                }
            }
        }
    }
                    when (selectedPage) {
                        "provinces" -> ProvincePageContent(
                            names = provinceNames, descriptions = provinceDescriptions,
                            prosperity = provinceProsperity, loyalty = provinceLoyalty, stewards = provinceStewards,
                            taxAvailable = provinceTaxDays.mapIndexed { i, day -> day < worldDay && provinceCrisisDays.getOrElse(i) { 0 } < 3 },
                            crisisDays = provinceCrisisDays,
                            gold = gold, influence = influence, report = provinceReport,
                            onCollectTax = { i ->
                                if (i in provinceNames.indices && provinceTaxDays[i] < worldDay && provinceCrisisDays[i] < 3) {
                                    val name = provinceNames[i]; val income = 5 + provinceProsperity[i] / 20
                                    gold = ResourceRules.addGold(gold, income); provinceLoyalty[i] = (provinceLoyalty[i] - 7).coerceAtLeast(0)
                                    provinceTaxDays[i] = worldDay
                                    provinceReport = "Сбор налогов в провинции «$name» принёс $income золота. Верность жителей снизилась. Следующий сбор будет доступен завтра."
                                    addJournalEntry("Провинции: налоги — $name, +$income золота."); save()
                                } else if (i in provinceNames.indices && provinceCrisisDays[i] >= 3) {
                                    provinceReport = "В «${provinceNames[i]}» восстание: сбор налогов невозможен, сначала урегулируй кризис."
                                } else provinceReport = "Налоги в этой провинции уже собраны сегодня. Вернись после смены игрового дня."
                            },
                            onDevelop = { i ->
                                val name = provinceNames[i]
                                if (provinceProsperity[i] >= 100) {
                                    provinceReport = "Провинция «$name» уже достигла максимального процветания."
                                } else if (gold >= 12) {
                                    gold -= 12; provinceProsperity[i] = (provinceProsperity[i] + 12).coerceAtMost(100)
                                    provinceLoyalty[i] = (provinceLoyalty[i] + 4).coerceAtMost(100)
                                    provinceReport = "Вложения в «$name» укрепили хозяйство и улучшили отношение жителей."
                                    addJournalEntry("Провинции: инвестиции — $name."); save()
                                } else provinceReport = "Для развития провинции требуется 12 золота."
                            },
                            onAppointSteward = { i ->
                                val name = provinceNames[i]
                                if (provinceStewards[i] == 0 && influence >= 3) {
                                    influence -= 3; provinceStewards[i] = 1
                                    provinceLoyalty[i] = (provinceLoyalty[i] + 10).coerceAtMost(100)
                                    provinceReport = "Наместник назначен в «$name». Управление стало устойчивее."
                                    addJournalEntry("Провинции: назначен наместник — $name."); save()
                                } else provinceReport = if (provinceStewards[i] == 1) "В этой провинции уже служит наместник." else "Для назначения наместника нужно 3 влияния."
                            },
                            onDismissSteward = { i ->
                                if (i in provinceNames.indices && provinceStewards[i] == 1) {
                                    provinceStewards[i] = 0
                                    provinceLoyalty[i] = (provinceLoyalty[i] - 3).coerceAtLeast(0)
                                    provinceReport = "Наместник провинции «${provinceNames[i]}» отстранён. Жалованье больше не начисляется, но жители недовольны сменой управления."
                                    addJournalEntry("Провинции: наместник отстранён — ${provinceNames[i]}.")
                                    save()
                                }
                            },
                            onResolveCrisis = { i, useInfluence ->
                                if (i in provinceNames.indices && provinceCrisisDays[i] > 0) {
                                    val name = provinceNames[i]
                                    val paymentSucceeded = if (useInfluence) {
                                        if (influence >= 4) {
                                            influence -= 4
                                            provinceLoyalty[i] = (provinceLoyalty[i] + 20).coerceAtMost(100)
                                            provinceReport = "Переговоры с общинами «$name» обошлись в 4 влияния. Кризис удалось остановить без расходов казны."
                                            true
                                        } else {
                                            provinceReport = "Для переговоров требуется 4 влияния."
                                            false
                                        }
                                    } else {
                                        if (gold >= 12) {
                                            gold -= 12
                                            provinceLoyalty[i] = (provinceLoyalty[i] + 25).coerceAtMost(100)
                                            provinceProsperity[i] = (provinceProsperity[i] + 2).coerceAtMost(100)
                                            provinceReport = "В «$name» доставлены зерно и помощь за 12 золота. Верность выросла, хозяйство получило небольшую поддержку."
                                            true
                                        } else {
                                            provinceReport = "Для доставки помощи требуется 12 золота."
                                            false
                                        }
                                    }
                                    if (paymentSucceeded) {
                                        provinceCrisisDays[i] = 0
                                        addJournalEntry("Провинции: урегулирован кризис в «$name».")
                                        save()
                                    }
                                }
                            }
                        )
                        "realm_map" -> RenderPage_realm_map()
                        "economy" -> EconomyPageContent(
                            gold = gold, treasuryLevel = treasuryLevel, lastTaxDay = lastTaxDay,
                            worldDay = worldDay, provisions = provisions, dailyRations = GameRules.dailyArmyRations(royalArmy), report = realmReport,
                            onCollectTaxes = {
                                val (result, nextTaxDay) = EconomyManager.collectTaxes(
                                    EconomyState(gold, provisions, royalArmy, guard, treasuryLevel), lastTaxDay, worldDay, merchantRep
                                )
                                when (result) {
                                    is EconomyActionResult.Applied -> {
                                        gold = result.state.gold; lastTaxDay = nextTaxDay
                                        realmReport = result.message; result.journalEntry?.let { addJournalEntry(it) }; save()
                                    }
                                    is EconomyActionResult.Rejected -> realmReport = result.message
                                }
                            },
                            onUpgradeTreasury = {
                                when (val result = EconomyManager.upgradeTreasury(EconomyState(gold, provisions, royalArmy, guard, treasuryLevel))) {
                                    is EconomyActionResult.Applied -> {
                                        gold = result.state.gold; treasuryLevel = result.state.treasuryLevel
                                        realmReport = result.message; result.journalEntry?.let { addJournalEntry(it) }; save()
                                    }
                                    is EconomyActionResult.Rejected -> realmReport = result.message
                                }
                            },
                            onBuyProvisions = {
                                when (val result = EconomyManager.buyProvisions(EconomyState(gold, provisions, royalArmy, guard, treasuryLevel))) {
                                    is EconomyActionResult.Applied -> {
                                        gold = result.state.gold; provisions = result.state.provisions
                                        realmReport = result.message; result.journalEntry?.let { addJournalEntry(it) }; save()
                                    }
                                    is EconomyActionResult.Rejected -> realmReport = result.message
                                }
                            }
                        )
                        "army" -> ArmyPageContent(
                            royalArmy = royalArmy, provisions = provisions, dailyRations = GameRules.dailyArmyRations(royalArmy), guard = guard,
                            gold = gold, report = realmReport,
                            onRecruit = {
                                when (val result = EconomyManager.recruit(EconomyState(gold, provisions, royalArmy, guard, treasuryLevel))) {
                                    is EconomyActionResult.Applied -> {
                                        gold = result.state.gold; provisions = result.state.provisions; royalArmy = result.state.armySize
                                        realmReport = result.message; result.journalEntry?.let { addJournalEntry(it) }; save()
                                    }
                                    is EconomyActionResult.Rejected -> realmReport = result.message
                                }
                            },
                            onTrain = {
                                when (val result = EconomyManager.trainGuard(EconomyState(gold, provisions, royalArmy, guard, treasuryLevel))) {
                                    is EconomyActionResult.Applied -> {
                                        provisions = result.state.provisions; guard = result.state.guard
                                        grantExperience(15); realmReport = result.message
                                        result.journalEntry?.let { addJournalEntry(it) }; advanceWorldTime(3); save()
                                    }
                                    is EconomyActionResult.Rejected -> realmReport = result.message
                                }
                            }
                        )
                        "frontier" -> RenderPage_frontier()
                        "battle" -> RenderPage_battle()
                        "hero" -> HeroPageContent(
                            health = health, reputation = reputation, guard = guard, influence = influence,
                            gold = gold, heroLevel = heroLevel, experience = experience,
                            powerSkill = powerSkill, guardSkill = guardSkill, skillPoints = skillPoints,
                            onUpgradePower = { if (skillPoints > 0 && powerSkill < 10) { powerSkill++; skillPoints--; save() } },
                            onUpgradeGuard = { if (skillPoints > 0 && guardSkill < 10) { guardSkill++; skillPoints--; save() } }
                        )
                        "dialogues" -> RenderPage_dialogues()
                        "people" -> PeoplePageContent(
                            garrettMemory = garrettMemory, selesteMemory = selesteMemory,
                            guard = guard, seleste = seleste, thomasMemory = thomasMemory,
                            thomasBond = thomasBond, miraMemory = miraMemory, miraBond = miraBond,
                            reynarBond = reynarBond, socialEvent = socialEvent,
                            socialConflict = socialConflict, socialEventFlags = socialEventFlags, socialActionFlags = socialActionFlags,
                            onDiscussCourtTies = {
                                if (socialActionFlags and 1 == 0) {
                                socialActionFlags = socialActionFlags or 1
                                guard = (guard + 2).coerceAtMost(100)
                                seleste = (seleste + 1).coerceAtMost(100)
                                socialConflict = (socialConflict + 1).coerceAtMost(3)
                                socialEvent = "Гаррет не одобряет тайные договорённости Селесты. Он предупредил тебя: однажды дворцовые интриги могут поставить стражу перед невозможным выбором."
                                addJournalEntry("Отношения: Гаррет выразил недоверие методам Селесты.")
                                save()
                                }
                            },
                            onHelpMira = {
                                if (socialActionFlags and 2 == 0) {
                                socialActionFlags = socialActionFlags or 2
                                miraBond = (miraBond + 5).coerceAtMost(100)
                                miraMemory++
                                gold = ResourceRules.addGold(gold, 2)
                                socialEvent = "Мира передала Томасу немного еды для ночной смены. Он теперь обязан ей услугой."
                                addJournalEntry("Отношения: помог Мире поддержать Томаса.")
                                save()
                                }
                            },
                            onMeetReynar = {
                                if (socialActionFlags and 4 == 0) {
                                socialActionFlags = socialActionFlags or 4
                                reynarBond = (reynarBond + 4).coerceAtMost(100)
                                influence = ResourceRules.addInfluence(influence, 2)
                                socialConflict = (socialConflict - 1).coerceAtLeast(0)
                                socialEvent = "Рейнар оценил твою сдержанность на переговорах. Напряжение между придворными немного ослабло, но герцог явно ждёт ответной услуги."
                                addJournalEntry("Отношения: заключено временное перемирие с герцогом Рейнаром.")
                                save()
                                }
                            },
                            onSelesteMoment = {
                                if (socialEventFlags and 1 == 0) {
                                socialEventFlags = socialEventFlags or 1
                                seleste = (seleste + 6).coerceAtMost(100)
                                influence = ResourceRules.addInfluence(influence, 1)
                                socialEvent = "Селеста впервые рассказала Эдриану о страхе потерять право самой выбирать свою судьбу. Между вами возникло осторожное доверие."
                                addJournalEntry("Отношения: тайный разговор с Селестой в оранжерее.")
                                save()
                                }
                            },
                            onReynarSecret = {
                                if (socialEventFlags and 2 == 0) {
                                socialEventFlags = socialEventFlags or 2
                                reynarBond = (reynarBond + 5).coerceAtMost(100)
                                influence = ResourceRules.addInfluence(influence, 2)
                                socialEvent = "Рейнар признался, что его поддержка не бескорыстна: он хочет видеть Эдриана своим посредником при дворе."
                                addJournalEntry("Отношения: Рейнар предложил тайный политический союз.")
                                save()
                                }
                            }
                        )
                        "world" -> RenderPage_world()
                        "council" -> RenderPage_council()
                        "companions" -> RenderPage_companions()
                        "houses" -> RenderPage_houses()
                        "life" -> RenderPage_life()
                        "quests" -> RenderPage_quests()
                        "expeditions" -> RenderPage_expeditions()
                        "items" -> InventoryPageContent(gold = gold, inventory = inventory)
                        "journal" -> JournalPageContent(entries = journal)
                        "more" -> RenderPage_more()
                        "about" -> RenderPage_about()
                        else -> RenderPage_fallback()
                    }
                }
                if (!fullWidthPages) Column(Modifier.weight(if (ultraCompact) 0.96f else 1.18f).fillMaxHeight().background(Brush.verticalGradient(listOf(Color(0xFF725333), Color(0xFF3A2618), Color(0xFF21150F))), RoundedCornerShape(4.dp)).border(3.dp, Color(0xFF5B3B22), RoundedCornerShape(4.dp)).padding(3.dp).border(1.dp, Color(0xFFD0AC70), RoundedCornerShape(3.dp)).padding(if (ultraCompact) 4.dp else 8.dp)) {
                    PageArtworkPanel(
                        selectedPage = selectedPage,
                        scene = scene,
                        sceneIndex = sceneIndex,
                        dialogNpc = dialogNpc,
                        currentLocation = currentLocation,
                        roseQuestStage = roseQuestStage,
                        worldDay = worldDay,
                        ultraCompact = ultraCompact,
                        compactLandscape = compactLandscape
                    )
                }
                }
                // Secondary systems are grouped under «Ещё»; the book now keeps one navigation rail.
            }
            Spacer(Modifier.height(if (ultraCompact) 2.dp else 5.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Text("ЭДРИАН ВЕЙЛ · $currentLocation", modifier = Modifier.weight(1f), color = Gold, fontSize = if (ultraCompact) 7.sp else 9.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(horizontalArrangement = Arrangement.spacedBy(if (ultraCompact) 0.dp else 8.dp)) { TextButton(onClick = { selectedPage = "journal" }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) { Text("Хроника", color = Paper, fontSize = 10.sp) }; TextButton(onClick = { confirmNewGame = true }, contentPadding = PaddingValues(horizontal = 4.dp, vertical = 0.dp)) { Text("Новая игра", color = Gold, fontSize = 10.sp) } }
                if (!ultraCompact) Text("ASHES OF THE CROWN  ·  6.0.0-alpha27", color = Color.LightGray, fontSize = 9.sp, letterSpacing = 1.sp, maxLines = 1)
            }
        }
        if (menuVisible) {
            MainMenuOverlay(
                currentLocation = currentLocation,
                prefs = prefs,
                musicEnabled = musicEnabled,
                slotRefresh = slotRefresh,
                onContinue = { menuVisible = false; selectedPage = "story" },
                onSaveSlot = { slot -> saveSlot(slot); slotRefresh++ },
                onLoadSlot = { slot -> loadSlot(slot) },
                onToggleMusic = {
                    musicEnabled = !musicEnabled
                    prefs.edit().putBoolean("music_enabled", musicEnabled).apply()
                },
                onAbout = { menuVisible = false; selectedPage = "about" }
            )
        }
        if (confirmNewGame) {
            AlertDialog(
                onDismissRequest = { confirmNewGame = false },
                title = { Text("Начать новую хронику?", fontFamily = FontFamily.Serif, fontWeight = FontWeight.Bold) },
                text = { Text("Текущий прогресс останется в основном сохранении только после отдельного сохранения в слот. Начать новую игру?", fontFamily = FontFamily.Serif) },
                confirmButton = { TextButton(onClick = { resetNewGame() }) { Text("Начать заново", color = Color(0xFF8C382B)) } },
                dismissButton = { TextButton(onClick = { confirmNewGame = false }) { Text("Отмена") } },
                containerColor = Paper,
                titleContentColor = Ink,
                textContentColor = Ink
            )
        }
    }
}



@Composable
private fun PeoplePageContent(
    garrettMemory: Int, selesteMemory: Int, guard: Int, seleste: Int,
    thomasMemory: Int, thomasBond: Int, miraMemory: Int, miraBond: Int,
    reynarBond: Int, socialEvent: String, socialConflict: Int, socialEventFlags: Int, socialActionFlags: Int,
    onDiscussCourtTies: () -> Unit, onHelpMira: () -> Unit,
    onMeetReynar: () -> Unit, onSelesteMoment: () -> Unit,
    onReynarSecret: () -> Unit
) {
    PaperHeading("ОТНОШЕНИЯ · ПАМЯТЬ · ЛИЧНЫЕ ИНТЕРЕСЫ")
    Text("Персонажи помнят помощь, угрозы и обещания. Их связи друг с другом тоже меняются: союзники могут спорить, а соперники — использовать твои решения.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
    Spacer(Modifier.height(8.dp))
    val relationCards = listOf(
        RelationPortraitData(R.drawable.portrait_garrett, "Капитан Гаррет Морн", "Верность долгу · память о встречах: $garrettMemory", guard, "Доверие"),
        RelationPortraitData(R.drawable.portrait_celeste, "Леди Селеста", "Осторожная близость · память о встречах: $selesteMemory", (seleste + 50).coerceIn(0,100), "Расположение"),
        RelationPortraitData(R.drawable.portrait_thomas, "Томас", "Ночной свидетель порта · память: $thomasMemory", (thomasBond + 100) / 2, "Дружба"),
        RelationPortraitData(R.drawable.portrait_mira, "Мира", "Торговка Нижнего города · память: $miraMemory", (miraBond + 100) / 2, "Доверие"),
        RelationPortraitData(R.drawable.portrait_reynar, "Герцог Рейнар", "Претендент на регентство", (reynarBond + 100) / 2, "Отношение")
    )
    BoxWithConstraints(Modifier.fillMaxWidth()) {
        val columns = CollectionLayoutRules.columns(maxWidth.value.toInt())
        relationCards.chunked(columns).forEach { rowCards ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rowCards.forEach { card ->
                    PortraitRelationCard(card.imageId, card.name, card.role, card.value, card.label, Modifier.weight(1f))
                }
                repeat((columns - rowCards.size).coerceAtLeast(0)) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
    Text("СВЯЗИ МЕЖДУ ПЕРСОНАЖАМИ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    Text(socialEvent, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
    if (socialConflict > 0) Text("Напряжение между союзниками: $socialConflict/3", color = Color(0xFF8B3428), fontWeight = FontWeight.Bold, fontSize = 11.sp)
    Button(onClick = onDiscussCourtTies, enabled = (socialActionFlags and 1) == 0, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if ((socialActionFlags and 1) == 0) "Обсудить с Гарретом тайные связи двора" else "Разговор с Гарретом уже состоялся", color = Paper) }
    Button(onClick = onHelpMira, enabled = (socialActionFlags and 2) == 0, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if ((socialActionFlags and 2) == 0) "Помочь Мире передать припасы Томасу · +2 золота" else "Ты уже помог Мире передать припасы", color = Paper) }
    Button(onClick = onMeetReynar, enabled = (socialActionFlags and 4) == 0, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if ((socialActionFlags and 4) == 0) "Провести дипломатическую встречу с Рейнаром" else "Встреча с Рейнаром уже состоялась", color = Paper) }
    if (seleste >= 20 && (socialEventFlags and 1) == 0) {
        Spacer(Modifier.height(6.dp))
        Text("ЛИЧНЫЙ МОМЕНТ · ЗАКРЫТАЯ ОРАНЖЕРЕЯ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        Text("Селеста предлагает поговорить без свидетелей. Это не обещание любви, а осторожный шаг к доверию — и возможность узнать, чего она боится больше всего.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
        Button(onClick = onSelesteMoment, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Выслушать Селесту · укрепить доверие", color = Ink) }
    }
    if (reynarBond >= 15 && (socialEventFlags and 2) == 0) {
        Button(onClick = onReynarSecret, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Выслушать тайное предложение Рейнара", color = Paper) }
    }
}

@Composable
private fun InventoryPageContent(gold: Int, inventory: List<String>) {
    PaperHeading("ИНВЕНТАРЬ")
    Text("Золото: $gold · Предметов: ${inventory.size}", color = Ink, fontWeight = FontWeight.Bold)
    Spacer(Modifier.height(8.dp))
    if (inventory.isEmpty()) {
        Text("Сумка пуста. Исследуй мир, выполняй задания и посещай торговцев.", color = Muted, fontSize = 13.sp)
    } else {
        val categoryTitles = mapOf(
            InventoryCategory.WEAPON to "ОРУЖИЕ",
            InventoryCategory.ARMOR to "БРОНЯ",
            InventoryCategory.CONSUMABLE to "РАСХОДНИКИ",
            InventoryCategory.MATERIAL to "МАТЕРИАЛЫ",
            InventoryCategory.QUEST to "КЛЮЧЕВЫЕ ПРЕДМЕТЫ",
            InventoryCategory.DOCUMENT to "ДОКУМЕНТЫ И УЛИКИ",
            InventoryCategory.TREASURE to "ЦЕННОСТИ",
            InventoryCategory.MISC to "ПРОЧЕЕ"
        )
        val grouped = inventory.groupingBy { it }.eachCount().entries.groupBy { InventoryManager.category(it.key) }
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val columns = CollectionLayoutRules.columns(maxWidth.value.toInt())
            InventoryCategory.values().forEach { category ->
                val entries = grouped[category].orEmpty().sortedBy { it.key }
                if (entries.isNotEmpty()) {
                    Text(categoryTitles[category] ?: category.name, color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    entries.chunked(columns).forEach { rowEntries ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                            rowEntries.forEach { entry ->
                                Row(
                                    Modifier.weight(1f).padding(vertical = 2.dp)
                                        .background(Color(0xFFDCC9A4), RoundedCornerShape(5.dp))
                                        .border(1.dp, Color(0xFFB49A70), RoundedCornerShape(5.dp))
                                        .padding(horizontal = 8.dp, vertical = 7.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("▣", color = Leather, fontSize = 12.sp)
                                    Spacer(Modifier.width(6.dp))
                                    Text(entry.key, modifier = Modifier.weight(1f), color = Ink, fontSize = 11.sp, lineHeight = 14.sp)
                                    if (entry.value > 1) Text("×${entry.value}", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            repeat((columns - rowEntries.size).coerceAtLeast(0)) { Spacer(Modifier.weight(1f)) }
                        }
                    }
                    Spacer(Modifier.height(7.dp))
                }
            }
        }
    }
    Spacer(Modifier.height(10.dp))
    Text("Уникальные предметы не дублируются. Материалы и расходники могут накапливаться; изготовление проверяет весь рецепт до списания ресурсов.", color = Muted, fontSize = 12.sp)
}

@Composable
private fun JournalPageContent(entries: List<String>) {
    PaperHeading("ЛЕТОПИСЬ СОБЫТИЙ")
    if (entries.isEmpty()) Text("Твои решения появятся здесь.", color = Ink)
    else entries.forEachIndexed { index, entry ->
        Text("${index + 1}. $entry", color = Ink, fontSize = 13.sp, lineHeight = 18.sp)
        Spacer(Modifier.height(7.dp))
    }
}

@Composable
private fun HeroPageContent(
    health: Int, reputation: Int, guard: Int, influence: Int, gold: Int,
    heroLevel: Int, experience: Int, powerSkill: Int, guardSkill: Int, skillPoints: Int,
    onUpgradePower: () -> Unit, onUpgradeGuard: () -> Unit
) {
    PaperHeading("ЛИЧНОЕ ДЕЛО")
    Image(painter = painterResource(R.drawable.portrait_adrian), contentDescription = "Эдриан Вейл", modifier = Modifier.fillMaxWidth().height(150.dp).border(2.dp, Gold, RoundedCornerShape(5.dp)), contentScale = ContentScale.Crop)
    Spacer(Modifier.height(8.dp))
    Text("Эдриан Вейл", color = Ink, fontSize = 25.sp, fontWeight = FontWeight.Bold)
    Text("24 года · хранитель королевских архивов", color = Muted, fontSize = 12.sp)
    Spacer(Modifier.height(12.dp))
    StatLine("Здоровье", health, 100)
    StatLine("Репутация", reputation.coerceIn(0,100), 100)
    StatLine("Доверие стражи", guard, 100)
    StatLine("Влияние при дворе", influence.coerceIn(0,100), 100)
    Text("Золото: $gold", color = Ink, fontSize = 14.sp)
    Spacer(Modifier.height(8.dp))
    Text("УРОВЕНЬ $heroLevel · ОПЫТ $experience / ${heroLevel * 100}", color = Muted, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    StatLine("Сила", powerSkill, 10)
    StatLine("Стойкость", guardSkill, 10)
    Text("Очки навыков: $skillPoints", color = Ink, fontSize = 13.sp)
    if (skillPoints > 0) {
        Button(onClick = onUpgradePower, enabled = powerSkill < 10, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text(if (powerSkill < 10) "Усилить атаку (+2 урона)" else "Сила достигла максимума", color = Ink) }
        Button(onClick = onUpgradeGuard, enabled = guardSkill < 10, colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (guardSkill < 10) "Усилить защиту (+2 брони)" else "Стойкость достигла максимума", color = Paper) }
    }
}

@Composable
private fun PageArtworkPanel(
    selectedPage: String,
    scene: Scene,
    sceneIndex: Int,
    dialogNpc: String,
    currentLocation: String,
    roseQuestStage: Int,
    worldDay: Int,
    ultraCompact: Boolean,
    compactLandscape: Boolean
) {
    val artwork = resolvePageArtwork(
        selectedPage, scene, sceneIndex, dialogNpc, currentLocation,
        roseQuestStage, worldDay
    )
    Box(
        Modifier.fillMaxWidth()
            .fillMaxHeight()
            .background(Brush.radialGradient(listOf(Color(0xFF806143), Color(0xFF211914))))
            .border(3.dp, Color(0xFF4B301C), RoundedCornerShape(3.dp))
            .padding(3.dp)
            .border(1.dp, Color(0xFFD6B77E), RoundedCornerShape(2.dp)),
        contentAlignment = Alignment.BottomStart
    ) {
        Image(
            painter = painterResource(id = artwork.drawable),
            contentDescription = "Сцена: ${artwork.caption}",
            modifier = Modifier.fillMaxSize(),
            // Иллюстрация заполняет книжное окно; исходники подготовлены в широком формате 16:9.
            contentScale = ContentScale.Crop
        )
    }
}

private data class PageArtwork(val drawable: Int, val eyebrow: String, val caption: String)

private fun resolvePageArtwork(
    selectedPage: String,
    scene: Scene,
    sceneIndex: Int,
    dialogNpc: String,
    currentLocation: String,
    roseQuestStage: Int,
    worldDay: Int
): PageArtwork {
val pageArtworkBase = when (selectedPage) {
                        "story" -> when (sceneIndex) {
                            0 -> R.drawable.story_v89_00
                            1 -> R.drawable.story_v89_01
                            2 -> R.drawable.story_v89_02
                            3 -> R.drawable.story_v89_03
                            4 -> R.drawable.story_v89_04
                            5 -> R.drawable.story_v89_05
                            6 -> R.drawable.story_v89_06
                            7 -> R.drawable.story_v89_07
                            8 -> R.drawable.story_v89_08
                            9 -> R.drawable.story_v89_09
                            10 -> R.drawable.story_v89_10
                            11 -> R.drawable.story_v89_11
                            12 -> R.drawable.story_v89_12
                            13 -> R.drawable.story_v89_13
                            14 -> R.drawable.story_v89_14
                            15 -> R.drawable.story_v89_15
                            16 -> R.drawable.story_v89_16
                            17 -> R.drawable.story_v89_17
                            18 -> R.drawable.story_v89_18
                            19 -> R.drawable.story_v89_19
                            20 -> R.drawable.story_v89_20
                            else -> R.drawable.story_v89_20
                        }
                        "world", "realm_map" -> R.drawable.great_houses_concepts_kingdom_overview
                        "hero" -> R.drawable.main_cast_adrian_portrait_concept
                        "people", "companions", "dialogues" -> when (dialogNpc) {
                            "Гаррет Морн" -> R.drawable.character_scenes_garrett_scene
                            "Селеста" -> R.drawable.character_scenes_celesta_scene
                            "Мира" -> R.drawable.character_scenes_mira_scene
                            "Томас" -> R.drawable.character_scenes_thomas_scene
                            else -> R.drawable.character_scenes_reynar_scene
                        }
                        "houses" -> when (currentLocation) {
                            "Крепость Вальден" -> R.drawable.great_houses_concepts_house_fortress_one
                            "Северная застава" -> R.drawable.great_houses_concepts_mountain_keep
                            else -> R.drawable.great_houses_concepts_great_houses_hall
                        }
                        "items" -> R.drawable.castle_interiors_armory_interior
                        "army" -> R.drawable.warfare_variants_2_army_camp
                        "battle" -> when (sceneIndex) {
                            18 -> R.drawable.story_events_final_battle_meeting
                            17 -> R.drawable.warfare_variants_2_victory_scene
                            16 -> R.drawable.warfare_gate_assault
                            else -> R.drawable.warfare_cavalry_charge
                        }
                        "journal", "quests" -> when (roseQuestStage) {
                            0 -> R.drawable.story_events_secret_letter
                            1 -> R.drawable.dangerous_places_secret_passage
                            else -> R.drawable.story_events_castle_escape
                        }
                        "provinces" -> when (currentLocation) {
                            "Крепость Вальден" -> R.drawable.landscape_locations_abandoned_fortress
                            "Воронья деревня" -> R.drawable.travel_locations_village_road
                            "Лесной тракт" -> R.drawable.nature_locations_dense_forest
                            "Северная застава" -> R.drawable.landscape_locations_winter_castle
                            else -> R.drawable.landscape_locations_mountain_pass
                        }
                        "economy" -> when (currentLocation) {
                            "Нижний город" -> R.drawable.city_locations_poor_district
                            "Порт Веймар", "Порт Эстервала" -> R.drawable.city_locations_harbor_tavern
                            else -> R.drawable.city_locations_craft_workshop
                        }
                        "council" -> when (sceneIndex) {
                            18 -> R.drawable.dramatic_events_war_declaration
                            17 -> R.drawable.story_scenes_2_court_audience
                            else -> R.drawable.story_scenes_2_council_meeting
                        }
                        "frontier", "expeditions" -> when (currentLocation) {
                            "Воронья деревня" -> R.drawable.travel_locations_village_road
                            "Лесной тракт" -> R.drawable.travel_locations_forest_path
                            "Северная застава" -> R.drawable.travel_locations_castle_gate
                            "Старая часовня" -> R.drawable.religious_sites_variants_chapel_path
                            else -> R.drawable.travel_locations_mountain_route
                        }
                        "life" -> when (currentLocation) {
                            "Нижний город" -> R.drawable.daily_life_harvest_festival
                            "Порт Веймар", "Порт Эстервала" -> R.drawable.story_and_people_city_music
                            else -> R.drawable.daily_life_royal_feast
                        }
                        "about" -> R.drawable.capital_panorama_variants_estervale_sunset_panorama
                        else -> R.drawable.capital_panorama_capital_city_panorama
                    }
val pageEyebrow = when (selectedPage) {
                        "story" -> "ХРОНИКА · ${scene.chapter}"
                        "world", "realm_map" -> "АТЛАС КОРОЛЕВСТВА"
                        "hero" -> "ЛИЧНОЕ ДЕЛО ХРАНИТЕЛЯ АРХИВОВ"
                        "people", "houses", "companions", "dialogues" -> "ЛЮДИ И ВЛИЯНИЕ"
                        "items", "army", "battle" -> "АРСЕНАЛ И СНАРЯЖЕНИЕ"
                        "journal", "quests" -> "ЗАПИСИ И СВИДЕТЕЛЬСТВА"
                        "provinces", "economy" -> "ВЛАДЕНИЯ КОРОНЫ"
                        "council" -> "СОВЕТ СЕМИ ДОМОВ"
                        "frontier", "expeditions" -> "СЕВЕРНАЯ МАРКА"
                        "life" -> "ПОВСЕДНЕВНАЯ ЖИЗНЬ ЭСТЕРВАЛЯ"
                        else -> "ПЕПЕЛ КОРОНЫ"
                    }
val pageCaption = when (selectedPage) {
                        "story" -> scene.title
                        "world", "realm_map" -> "Земли Эстервальского королевства"
                        "hero" -> "Эдриан Вейл · Хранитель архивов"
                        "people", "houses", "companions", "dialogues" -> "Знакомые лица и великие дома"
                        "items", "army", "battle" -> "Сталь, припасы и снаряжение"
                        "journal", "quests" -> "Память о принятых решениях"
                        "provinces", "economy" -> "Земли, казна и верность подданных"
                        "council" -> "Голоса знати решают судьбу короны"
                        "frontier", "expeditions" -> "Дороги за северными рубежами"
                        "life" -> "Улицы, рынки и гавань"
                        else -> "Летопись Эстервала"
                    }
                    // Дополнительный каталог: все новые концепт-иллюстрации подключены к тематическим разделам.
                    // Выбор детерминированный (день/глава/этап задания), без случайной смены при перерисовке.
val assignedArtwork = when (selectedPage) {
                        "journal", "quests" -> listOf(R.drawable.story_drama_royal_confrontation, R.drawable.story_drama_love_confession, R.drawable.story_drama_farewell_before_battle, R.drawable.story_drama_private_conversation, R.drawable.story_events_city_siege, R.drawable.story_scenes_2_night_conspiracy, R.drawable.story_scenes_2_city_panorama)[(worldDay + sceneIndex + roseQuestStage).mod(7)]
                        "hero" -> listOf(R.drawable.character_concepts_king_concept, R.drawable.character_concepts_queen_concept, R.drawable.character_concepts_heir_concept, R.drawable.character_concepts_noblewoman_concept, R.drawable.character_concepts_knight_concept, R.drawable.character_concepts_sorceress_concept, R.drawable.character_concepts_2_royal_ruler, R.drawable.character_concepts_2_royal_woman, R.drawable.character_concepts_2_young_noble, R.drawable.character_concepts_2_lady_in_red, R.drawable.character_concepts_2_armored_knight, R.drawable.character_concepts_2_mage, R.drawable.main_cast_garrett_portrait_concept, R.drawable.main_cast_thomas_portrait_concept, R.drawable.main_cast_reynar_portrait_concept, R.drawable.main_cast_celesta_portrait_concept, R.drawable.main_cast_mira_portrait_concept, R.drawable.main_cast_concept_adrian_portrait_concept)[(worldDay + sceneIndex + roseQuestStage).mod(18)]
                        "frontier" -> listOf(R.drawable.dangerous_places_mercenary_camp, R.drawable.dangerous_places_prison, R.drawable.dangerous_places_torture_chamber, R.drawable.dangerous_places_abandoned_mine, R.drawable.historical_sites_grand_cathedral, R.drawable.historical_sites_old_monastery, R.drawable.historical_sites_royal_crypt, R.drawable.historical_sites_ancient_temple, R.drawable.historical_sites_roadside_chapel, R.drawable.historical_sites_ruined_shrine, R.drawable.religious_sites_variants_cathedral_exterior, R.drawable.religious_sites_variants_abbey_courtyard, R.drawable.religious_sites_variants_royal_tomb, R.drawable.religious_sites_variants_temple_ruins, R.drawable.religious_sites_variants_sanctuary_ruins)[(worldDay + sceneIndex + roseQuestStage).mod(15)]
                        "battle" -> listOf(R.drawable.warfare_army_muster, R.drawable.warfare_archers_on_walls, R.drawable.warfare_siege_engines, R.drawable.warfare_battle_victory, R.drawable.warfare_variants_battlefield_overview, R.drawable.warfare_variants_castle_defense, R.drawable.warfare_variants_army_advance, R.drawable.warfare_variants_siege_tower, R.drawable.warfare_variants_wall_breach, R.drawable.warfare_variants_aftermath)[(worldDay + sceneIndex + roseQuestStage).mod(10)]
                        "army" -> listOf(R.drawable.warfare_variants_2_battle_planning, R.drawable.warfare_variants_2_knight_duel, R.drawable.warfare_variants_2_siege_line, R.drawable.warfare_variants_2_city_defense)[(worldDay + sceneIndex + roseQuestStage).mod(4)]
                        "people" -> listOf(R.drawable.character_scenes_adrian_scene, R.drawable.story_and_people_royal_court, R.drawable.story_and_people_private_talk, R.drawable.story_and_people_street_scene, R.drawable.story_and_people_political_encounter, R.drawable.story_and_people_night_city)[(worldDay + sceneIndex + roseQuestStage).mod(6)]
                        "life" -> listOf(R.drawable.daily_life_field_hospital, R.drawable.daily_life_knight_tournament, R.drawable.daily_life_popular_uprising)[(worldDay + sceneIndex + roseQuestStage).mod(3)]
                        "houses" -> listOf(R.drawable.castle_interiors_castle_corridor, R.drawable.castle_interiors_private_chambers, R.drawable.castle_interiors_secret_room, R.drawable.great_houses_concepts_house_fortress_two, R.drawable.great_houses_concepts_house_fortress_three, R.drawable.castle_interiors_2_royal_bedchamber, R.drawable.castle_interiors_2_rulers_study, R.drawable.castle_interiors_2_reception_hall, R.drawable.castle_interiors_2_heirs_quarters, R.drawable.castle_interiors_2_arcane_chamber)[(worldDay + sceneIndex + roseQuestStage).mod(10)]
                        "world" -> listOf(R.drawable.nature_locations_waterfall, R.drawable.nature_locations_snowy_valley, R.drawable.nature_locations_mountain_lake)[(worldDay + sceneIndex + roseQuestStage).mod(3)]
                        "economy" -> listOf(R.drawable.city_locations_noble_quarter, R.drawable.city_locations_city_square, R.drawable.city_locations_night_alley)[(worldDay + sceneIndex + roseQuestStage).mod(3)]
                        "provinces" -> listOf(R.drawable.landscape_locations_autumn_forest, R.drawable.landscape_locations_ancient_ruins, R.drawable.landscape_locations_mountain_monastery)[(worldDay + sceneIndex + roseQuestStage).mod(3)]
                        "council" -> listOf(R.drawable.dramatic_events_coronation, R.drawable.dramatic_events_royal_wedding, R.drawable.dramatic_events_peace_treaty, R.drawable.dramatic_events_betrayal_reveal, R.drawable.dramatic_events_noble_trial)[(worldDay + sceneIndex + roseQuestStage).mod(5)]
                        else -> null
                    }
return PageArtwork(assignedArtwork ?: pageArtworkBase, pageEyebrow, pageCaption)
}

@Composable
private fun MainMenuOverlay(
    currentLocation: String,
    prefs: android.content.SharedPreferences,
    musicEnabled: Boolean,
    slotRefresh: Int,
    onContinue: () -> Unit,
    onSaveSlot: (Int) -> Unit,
    onLoadSlot: (Int) -> Unit,
    onToggleMusic: () -> Unit,
    onAbout: () -> Unit
) {

        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF493423), Night, Color(0xFF0C0908)))).padding(18.dp), contentAlignment = Alignment.Center) {
            Row(Modifier.fillMaxWidth().fillMaxHeight(0.92f).background(Paper, RoundedCornerShape(8.dp)).border(3.dp, Gold, RoundedCornerShape(8.dp)).padding(18.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(18.dp)) {
                Column(Modifier.weight(1.05f), verticalArrangement = Arrangement.Center) {
                    Image(painter = painterResource(R.drawable.estervale_book_art), contentDescription = "Обложка Пепла короны", modifier = Modifier.fillMaxWidth().height(190.dp).border(2.dp, Gold, RoundedCornerShape(5.dp)), contentScale = ContentScale.Crop)
                    Spacer(Modifier.height(10.dp))
                    Text("ХРОНИКИ ЭСТЕРВАЛЯ", color = Muted, fontSize = 10.sp, letterSpacing = 2.sp)
                    Text("ПЕПЕЛ КОРОНЫ", color = Ink, fontSize = 27.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
                    Text("ASHES OF THE CROWN · 6.0.0-alpha27", color = Muted, fontSize = 11.sp)
                }
                Column(Modifier.weight(1f).fillMaxHeight().verticalScroll(rememberScrollState()), verticalArrangement = Arrangement.Center) {
                    Text("ГЛАВНОЕ МЕНЮ", color = Ink, fontSize = 19.sp, fontWeight = FontWeight.Bold)
                    Text("Эдриан Вейл · $currentLocation", color = Muted, fontSize = 11.sp)
                    Spacer(Modifier.height(10.dp))
                    Button(onClick = { onContinue() }, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text("Продолжить", color = Ink) }
                    Text("СЛОТЫ СОХРАНЕНИЯ", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    key(slotRefresh) {
                        (1..3).forEach { slot ->
                        val label = prefs.getString("slot_${slot}_label", null)
                        Column(Modifier.fillMaxWidth().padding(vertical = 3.dp).background(Color(0xFFDCC9A4), RoundedCornerShape(5.dp)).border(1.dp, Color(0xFF9A7747), RoundedCornerShape(5.dp)).padding(7.dp)) {
                            Text("Слот $slot", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text(label ?: "Пустой слот", color = Muted, fontSize = 9.sp)
                            Row(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                                Button(onClick = { onSaveSlot(slot) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text("Сохранить", color = Paper, fontSize = 10.sp) }
                                OutlinedButton(onClick = { onLoadSlot(slot) }, enabled = label != null, modifier = Modifier.weight(1f), contentPadding = PaddingValues(4.dp), border = BorderStroke(1.dp, Gold)) { Text("Загрузить", color = Ink, fontSize = 10.sp) }
                            }
                        }
                        }
                    }
                    Spacer(Modifier.height(5.dp))
                    OutlinedButton(onClick = { onToggleMusic() }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Gold)) {
                        Text(if (musicEnabled) "♫ Выключить музыку" else "♫ Включить музыку", color = Ink)
                    }
                    OutlinedButton(onClick = { onAbout() }, modifier = Modifier.fillMaxWidth(), border = BorderStroke(1.dp, Gold)) { Text("Об игре", color = Ink) }
                    Text("Прогресс сохраняется после игровых действий · Музыка ${if (musicEnabled) "включена" else "выключена"}", color = Muted, fontSize = 9.sp)
                }
            }
        }

}

@Composable
private fun EconomyPageContent(
    gold: Int, treasuryLevel: Int, lastTaxDay: Int, worldDay: Int,
    provisions: Int, dailyRations: Int, report: String,
    onCollectTaxes: () -> Unit, onUpgradeTreasury: () -> Unit, onBuyProvisions: () -> Unit
) {
    PaperHeading("КОРОЛЕВСКАЯ КАЗНА И ХОЗЯЙСТВО")
    Text("Управляй доходами короны. Налоги можно собирать один раз за игровой день; улучшение казначейства повышает ежедневный сбор.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
    Spacer(Modifier.height(8.dp))
    StatLine("Золото в казне", gold, 150)
    StatLine("Уровень казначейства", treasuryLevel, 3)
    Text("Последний сбор: день $lastTaxDay · текущий день $worldDay", color = Muted, fontSize = 11.sp)
    Button(onClick = onCollectTaxes, enabled = worldDay >= 1 && lastTaxDay < worldDay, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
        Text(if (worldDay < 1 || lastTaxDay >= worldDay) "Налоги уже собраны или день недоступен" else "Собрать налоги · получить доход", color = Ink)
    }
    Button(onClick = onUpgradeTreasury, enabled = treasuryLevel < 3 && gold >= treasuryLevel * 25, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
        Text("Улучшить казначейство · ${treasuryLevel * 25} золота", color = Paper)
    }
    HorizontalDivider(color = Color(0xFFB49A70))
    Text("ПРОВИАНТ И СНАБЖЕНИЕ", color = Muted, fontWeight = FontWeight.Bold, fontSize = 11.sp)
    Text("Запасы: $provisions порций. Армия расходует $dailyRations порций при наступлении каждого нового дня.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
    Button(onClick = onBuyProvisions, enabled = gold >= 6 && provisions <= 90, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
        Text(when { provisions >= 100 -> "Склады заполнены · 100/100"; provisions > 90 -> "Недостаточно места для партии из 10 порций"; else -> "Закупить 10 порций · 6 золота" }, color = Paper)
    }
    Text(report, color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
}

@Composable
private fun ArmyPageContent(
    royalArmy: Int, provisions: Int, dailyRations: Int, guard: Int, gold: Int, report: String,
    onRecruit: () -> Unit, onTrain: () -> Unit
) {
    PaperHeading("КОРОЛЕВСКОЕ ВОЙСКО")
    Text("Собирай гарнизон, готовь солдат и следи за снабжением. Военная сила отражает размер войска, а не заменяет отдельные сюжетные сражения.", color = Ink, fontSize = 13.sp, lineHeight = 19.sp)
    Spacer(Modifier.height(8.dp))
    StatLine("Численность войска", royalArmy, 100)
    StatLine("Провиант", provisions, 100)
    StatLine("Доверие гвардии", guard, 100)
    Text("Ежедневное содержание: $dailyRations порций провианта. На каждые 10 солдат требуется 1 порция в день.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    Text("Если запасов не хватит при смене дня, войско понесёт потери, а доверие гвардии и верность провинций снизятся.", color = Muted, fontSize = 11.sp, lineHeight = 16.sp)
    Text("Набор: 5 солдат за 12 золота и 3 порции провианта.", color = Muted, fontSize = 11.sp)
    Button(onClick = onRecruit, enabled = gold >= 12 && provisions >= 3 && royalArmy <= 95, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Leather)) {
        Text(when { royalArmy >= 100 -> "Предельная численность · 100/100"; royalArmy > 95 -> "Недостаточно мест для набора пяти солдат"; else -> "Нанять 5 солдат · 12 золота + 3 провианта" }, color = Paper)
    }
    Button(onClick = onTrain, enabled = provisions >= 5 && guard < 100, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Gold)) {
        Text(if (guard >= 100) "Гвардия полностью подготовлена" else "Провести учения · 5 провианта · 3 часа", color = Ink)
    }
    Text("Состояние войска: ${if (royalArmy >= 50) "сильный гарнизон" else if (royalArmy >= 25) "боеспособный отряд" else "малочисленная дружина"}.", color = Ink, fontSize = 12.sp)
    Text(report, color = Ink, fontSize = 12.sp, lineHeight = 18.sp)
}

@Composable
private fun ProvincePageContent(
    names: List<String>, descriptions: List<String>, prosperity: List<Int>, loyalty: List<Int>, stewards: List<Int>,
    crisisDays: List<Int>, gold: Int, influence: Int, report: String, taxAvailable: List<Boolean>,
    onCollectTax: (Int) -> Unit, onDevelop: (Int) -> Unit,
    onAppointSteward: (Int) -> Unit, onDismissSteward: (Int) -> Unit,
    onResolveCrisis: (Int, Boolean) -> Unit
) {
    PaperHeading("ПРОВИНЦИИ КОРОНЫ")
    Text("Управляй благосостоянием земель, доверием жителей и работой наместников. Налоги дают казне золото, но чрезмерные сборы ослабляют верность провинций.", color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
    Spacer(Modifier.height(8.dp))
    Row(Modifier.fillMaxWidth().background(Color(0xFFD4C096), RoundedCornerShape(5.dp)).padding(8.dp), horizontalArrangement = Arrangement.SpaceBetween) {
        Text("КАЗНА  $gold золота", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        Text("ВЛИЯНИЕ  $influence", color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
    Spacer(Modifier.height(8.dp))
    names.forEachIndexed { i, name ->
        Column(Modifier.fillMaxWidth().padding(bottom = 9.dp).background(Color(0xFFE4D1AA), RoundedCornerShape(5.dp)).border(1.dp, Color(0xFF9B7A4D), RoundedCornerShape(5.dp)).padding(9.dp)) {
            Text("${i + 1}. $name", color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Serif)
            Text(descriptions.getOrElse(i) { "" }, color = Muted, fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            Text("Процветание: ${prosperity[i]}/100", color = Ink, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(progress = { prosperity[i] / 100f }, modifier = Modifier.fillMaxWidth().height(5.dp), color = Color(0xFF8A6A36), trackColor = Color(0xFFC6B18C))
            Text("Верность жителей: ${loyalty[i]}/100 · Наместник: ${if (stewards[i] == 1) "назначен" else "не назначен"}", color = Ink, fontSize = 11.sp)
            LinearProgressIndicator(progress = { loyalty[i] / 100f }, modifier = Modifier.fillMaxWidth().height(5.dp), color = Color(0xFF54704A), trackColor = Color(0xFFC6B18C))
            val crisis = crisisDays.getOrElse(i) { 0 }
            val crisisStatus = WorldCrisisManager.evaluate(
                listOf(name), listOf(loyalty.getOrElse(i) { 0 }),
                listOf(prosperity.getOrElse(i) { 0 }), listOf(crisis)
            ).provinces.first()
            Text(
                when (crisisStatus.stage) {
                    ProvinceCrisisStage.STABLE -> "Положение стабильно"
                    ProvinceCrisisStage.UNREST -> "⚠ ${crisisStatus.title} · ранние признаки недовольства"
                    ProvinceCrisisStage.ESCALATING -> "⚠ ${crisisStatus.title} · ${crisisStatus.days}/3 этапа"
                    ProvinceCrisisStage.REVOLT -> "⚠ ВОССТАНИЕ · налоги и доход приостановлены"
                },
                color = if (crisis > 0) Color(0xFF8A3024) else Muted,
                fontSize = 10.sp,
                fontWeight = if (crisis > 0) FontWeight.Bold else FontWeight.Normal
            )
            if (crisis > 0) Text(crisisStatus.recommendedAction, color = Muted, fontSize = 10.sp, lineHeight = 13.sp)
            Spacer(Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(5.dp), modifier = Modifier.fillMaxWidth()) {
                Button(onClick = { onCollectTax(i) }, enabled = taxAvailable.getOrElse(i) { false }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.buttonColors(containerColor = Leather)) { Text(if (taxAvailable.getOrElse(i) { false }) "Собрать налог" else "Собрано сегодня", color = Paper, fontSize = 10.sp) }
                Button(onClick = { onDevelop(i) }, modifier = Modifier.weight(1f), contentPadding = PaddingValues(4.dp), enabled = gold >= 12 && prosperity[i] < 100, colors = ButtonDefaults.buttonColors(containerColor = Gold)) { Text(if (prosperity[i] < 100) "Развить · 12" else "Максимум", color = Ink, fontSize = 10.sp) }
            }
            if (stewards[i] == 0) {
                Button(onClick = { onAppointSteward(i) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(4.dp), enabled = influence >= 3, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6E5838))) {
                    Text("Назначить наместника · 3 влияния", color = Paper, fontSize = 10.sp)
                }
            } else {
                Button(onClick = { onDismissSteward(i) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80634A))) {
                    Text("Отстранить наместника · без возврата влияния", color = Paper, fontSize = 10.sp)
                }
            }
            if (crisis > 0) {
                Button(onClick = { onResolveCrisis(i, false) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(4.dp), enabled = gold >= 12, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF8A3024))) {
                    Text("Доставить помощь · 12 золота", color = Paper, fontSize = 10.sp)
                }
                Button(onClick = { onResolveCrisis(i, true) }, modifier = Modifier.fillMaxWidth(), contentPadding = PaddingValues(4.dp), enabled = influence >= 4, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF76543B))) {
                    Text("Начать переговоры · 4 влияния", color = Paper, fontSize = 10.sp)
                }
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    Text("ДОКЛАД СОВЕТНИКА", color = Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold)
    Text(report, color = Ink, fontSize = 12.sp, lineHeight = 17.sp)
    val estimatedGross = names.indices.sumOf { i ->
        if (crisisDays.getOrElse(i) { 0 } >= 3) return@sumOf 0
        val loyaltyModifier = when {
            loyalty.getOrElse(i) { 0 } >= 75 -> 1
            loyalty.getOrElse(i) { 0 } < 25 -> -1
            else -> 0
        }
        (1 + prosperity.getOrElse(i) { 0 } / 25 + loyaltyModifier + if (stewards.getOrElse(i) { 0 } == 1) 2 else 0).coerceAtLeast(0)
    }
    val dailyWages = stewards.count { it == 1 }
    Text("Ежедневный доход начисляется автоматически при наступлении нового дня.", color = Muted, fontSize = 11.sp, lineHeight = 15.sp)
    Text("Прогноз: $estimatedGross золота · жалованье наместников $dailyWages · чистый доход ${estimatedGross - dailyWages} золота в день.", color = Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
}

@Composable private fun RealmIllustratedMap(currentLocation: String, sceneIndex: Int, roseQuestStage: Int, onTravel: (String, Int) -> Unit) {
    // Canonical world-map names are shared with the world page, quests and location events.
    Column(Modifier.fillMaxWidth().background(Brush.verticalGradient(listOf(Color(0xFFE9D7AE), Color(0xFFC9AD78))), RoundedCornerShape(7.dp)).border(2.dp, Color(0xFF8A6639), RoundedCornerShape(7.dp)).padding(9.dp)) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text("✥  ВЛАДЕНИЯ КОРОНЫ  ✥", color = Color(0xFF65482A), fontSize = 10.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.2.sp)
            Text("N ↑", color = Color(0xFF65482A), fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(5.dp))
        Box(Modifier.fillMaxWidth().height(24.dp).background(Color(0xFFB6C3A0), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text("❧ ЛЕСА ВАЛЬДЕНА  ·  СЕВЕРНЫЕ ХОЛМЫ ❧", color = Color(0xFF40513B), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            RealmMapNode("Крепость Вальден", "♜", currentLocation == "Крепость Вальден", TravelManager.findRoute(currentLocation, "Крепость Вальден")?.totalHours, unlocked = sceneIndex >= 5, modifier = Modifier.weight(1f), onTravel = onTravel)
            RealmMapNode("Зимний сад", "♧", currentLocation == "Зимний сад", TravelManager.findRoute(currentLocation, "Зимний сад")?.totalHours, unlocked = true, modifier = Modifier.weight(1f), onTravel = onTravel)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            RealmMapNode("Северная застава", "🛡", currentLocation == "Северная застава", TravelManager.findRoute(currentLocation, "Северная застава")?.totalHours, unlocked = sceneIndex >= 5, modifier = Modifier.weight(1f), onTravel = onTravel)
            RealmMapNode("Воронья деревня", "🌾", currentLocation == "Воронья деревня", TravelManager.findRoute(currentLocation, "Воронья деревня")?.totalHours, unlocked = sceneIndex >= 5, modifier = Modifier.weight(1f), onTravel = onTravel)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RealmMapNode("Лесной тракт", "🌲", currentLocation == "Лесной тракт", TravelManager.findRoute(currentLocation, "Лесной тракт")?.totalHours, unlocked = sceneIndex >= 5, onTravel = onTravel)
        }
        Box(Modifier.fillMaxWidth().padding(vertical = 5.dp).height(22.dp), contentAlignment = Alignment.Center) {
            Text("╲   ───── СТАРЫЙ КОРОЛЕВСКИЙ ТРАКТ ─────   ╱", color = Color(0xFF80613B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Box(Modifier.fillMaxWidth().background(Color(0xFFD8C18F), RoundedCornerShape(5.dp)).border(1.dp, Color(0xFF9A7848), RoundedCornerShape(5.dp)).padding(vertical = 7.dp, horizontal = 4.dp)) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                Text("ЭСТЕРВАЛЬ  ·  СТОЛИЦА", color = Color(0xFF70502D), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.3.sp)
                Spacer(Modifier.height(4.dp))
                RealmMapNode("Королевский замок", "✦", currentLocation == "Королевский замок", TravelManager.findRoute(currentLocation, "Королевский замок")?.totalHours, unlocked = true, onTravel = onTravel)
            }
        }
        Box(Modifier.fillMaxWidth().height(23.dp), contentAlignment = Alignment.Center) {
            Text("╲             │  СТАРАЯ ДОРОГА  │             ╱", color = Color(0xFF80613B), fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly, verticalAlignment = Alignment.CenterVertically) {
            RealmMapNode("Нижний город", "⚒", currentLocation == "Нижний город", TravelManager.findRoute(currentLocation, "Нижний город")?.totalHours, unlocked = true, modifier = Modifier.weight(1f), onTravel = onTravel)
            RealmMapNode("Рыночная площадь", "◇", currentLocation == "Рыночная площадь", TravelManager.findRoute(currentLocation, "Рыночная площадь")?.totalHours, unlocked = true, modifier = Modifier.weight(1f), onTravel = onTravel)
        }
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RealmMapNode("Старая часовня", "⛪", currentLocation == "Старая часовня", TravelManager.findRoute(currentLocation, "Старая часовня")?.totalHours, unlocked = roseQuestStage >= 1, onTravel = onTravel)
        }
        Spacer(Modifier.height(6.dp))
        Box(Modifier.fillMaxWidth().height(25.dp).background(Color(0xFF8FAEAD), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) {
            Text("≈≈≈  РЕКА ЭСТЕР  ·  ТОРГОВЫЙ ПУТЬ  ≈≈≈", color = Color(0xFF294D50), fontSize = 9.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
        }
        Spacer(Modifier.height(6.dp))
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
            RealmMapNode("Порт Веймар", "⚓", currentLocation == "Порт Веймар", TravelManager.findRoute(currentLocation, "Порт Веймар")?.totalHours, unlocked = sceneIndex >= 7, onTravel = onTravel)
        }
        Spacer(Modifier.height(4.dp))
        Text("ЗАПАД  ·  ЗЕМЛИ СЕМИ ДОМОВ                         ВОСТОК  ·  РЕЧНЫЕ ПРИЧАЛЫ", color = Color(0xFF725638), fontSize = 8.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
    }
}

@Composable private fun RealmMapNode(name: String, icon: String, selected: Boolean, hours: Int?, unlocked: Boolean = true, modifier: Modifier = Modifier, onTravel: (String, Int) -> Unit) {
    val active = selected
    Column(modifier.widthIn(min = 0.dp).background(if (active) Color(0xFFB58A4D) else Color(0xFFE9DAB8), RoundedCornerShape(6.dp)).border(2.dp, if (active) Color(0xFF6D4723) else Color(0xFF9B794A), RoundedCornerShape(6.dp)).padding(horizontal = 5.dp, vertical = 3.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = { hours?.let { onTravel(name, it) } }, enabled = !active && unlocked && hours != null, contentPadding = PaddingValues(horizontal = 2.dp, vertical = 0.dp), modifier = Modifier.fillMaxWidth()) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(if (unlocked) icon else "🔒", color = Color(0xFF644526), fontSize = 17.sp)
                Text(if (active) "ЗДЕСЬ · $name" else name, color = Color(0xFF3D2B1C), fontSize = 9.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 11.sp)
                if (!active) Text(when { !unlocked -> "Пока закрыто"; hours == null -> "Нет доступного пути"; else -> "Путь: $hours ч." }, color = Color(0xFF80613B), fontSize = 8.sp)
            }
        }
    }
}

@Composable private fun MapPlace(title: String, selected: Boolean, unlocked: Boolean, subtitle: String, onClick: () -> Unit) {
    Column(Modifier.padding(3.dp).widthIn(min = 112.dp).background(if (selected) Color(0xFFB99A68) else Color(0xFFE8D8B5), RoundedCornerShape(5.dp)).border(1.dp, if (unlocked) Color(0xFF876039) else Color(0xFF9A8A70), RoundedCornerShape(5.dp)).padding(5.dp), horizontalAlignment = Alignment.CenterHorizontally) {
        TextButton(onClick = onClick, enabled = unlocked, contentPadding = PaddingValues(horizontal = 3.dp, vertical = 1.dp)) { Text(if (unlocked) title else "🔒 $title", color = if (unlocked) Ink else Muted, fontSize = 10.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, lineHeight = 13.sp) }
        Text(if (unlocked) subtitle else "Пока недоступно", color = Muted, fontSize = 8.sp, textAlign = TextAlign.Center, lineHeight = 10.sp)
    }
}

@Composable private fun PaperHeading(text: String) { Text(text, color = Muted, fontSize = 10.sp, letterSpacing = 2.sp); Spacer(Modifier.height(7.dp)) }
private data class RelationPortraitData(val imageId: Int, val name: String, val role: String, val value: Int, val label: String)

@Composable private fun PortraitRelationCard(imageId: Int, name: String, role: String, value: Int, label: String, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val portrait = PortraitLayoutRules.relationPortrait(maxWidth.value.toInt())
        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp).background(Color(0xFFDCC9A4), RoundedCornerShape(6.dp)).border(1.dp, Color(0xFF9A7747), RoundedCornerShape(6.dp)).padding(6.dp), verticalAlignment = Alignment.CenterVertically) {
            Image(painter = painterResource(imageId), contentDescription = "Портрет: $name", modifier = Modifier.width(portrait.width.dp).height(portrait.height.dp).border(2.dp, Gold, RoundedCornerShape(4.dp)), contentScale = ContentScale.Crop)
            Spacer(Modifier.width(7.dp))
            Column(Modifier.weight(1f)) {
                Text(name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 12.sp, lineHeight = 14.sp)
                Text(role, color = Muted, fontSize = 9.sp, lineHeight = 12.sp)
                Spacer(Modifier.height(3.dp))
                StatLine(label, value.coerceIn(0, 100), 100)
            }
        }
    }
}

@Composable private fun RelationCard(name: String, role: String, value: Int, label: String) { Text(name, color = Ink, fontWeight = FontWeight.Bold, fontSize = 16.sp); Text(role, color = Muted, fontSize = 11.sp); Spacer(Modifier.height(5.dp)); StatLine(label, value, 100); Spacer(Modifier.height(7.dp)) }
@Composable private fun StatLine(name: String, value: Int, max: Int) { Column(Modifier.padding(vertical = 5.dp)) { Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) { Text(name, color = Ink, fontSize = 12.sp); Text("$value/$max", color = Ink, fontSize = 11.sp) }; LinearProgressIndicator(progress = { (value.toFloat()/max).coerceIn(0f,1f) }, modifier = Modifier.fillMaxWidth().height(5.dp), color = Color(0xFF876039), trackColor = Color(0xFFB99E72)) } }
