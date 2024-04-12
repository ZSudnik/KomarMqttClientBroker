package com.zibi.mod.common.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
//import com.google.accompanist.pager.HorizontalPagerIndicator
import kotlinx.coroutines.launch
import com.zibi.mod.common.ui.button.large.ButtonLarge
import com.zibi.mod.common.ui.button.large.ButtonLargeStyle
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R

data class OnboardingPageData(
  val title: String,
  val message: String,
  val imageResId: Int
)


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
  initialPage: Int = 0,
  pages: List<OnboardingPageData>,
  onResult: () -> Int
) {
  val pagerState = rememberPagerState(
    initialPage = if (initialPage < pages.size) initialPage else 0,
    pageCount = onResult
  )
  val coroutineScope = rememberCoroutineScope()

  Column(
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    LazyColumn(
      modifier = Modifier
        .weight(1f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
    ) {
      item {
        HorizontalPager(
          modifier = Modifier
            .fillMaxWidth()
            .weight(0.75f),
//          pageSize = pages.size,
          state = pagerState
        ) {
          OnboardingPage(page = pages[it])
        }
        Spacer(modifier = Modifier.height(AppTheme.dimensions.smallPadding))
      }
    }

//    HorizontalPagerIndicator(
//      pagerState = pagerState,
//      activeColor = AppTheme.colors.progressIndicator,
//      inactiveColor = AppTheme.colors.progressBackground
//    )

    Column(
      modifier = Modifier
        .fillMaxWidth()
        .wrapContentHeight()
        .padding(
          horizontal = AppTheme.dimensions.xRegularPadding
        ),
      verticalArrangement = Arrangement.Bottom,
      horizontalAlignment = Alignment.CenterHorizontally
    ) {
      Spacer(modifier = Modifier.height(AppTheme.dimensions.xRegularPadding))
      ButtonLarge(
        text = if (pagerState.currentPage == (pages.size - 1))
          stringResource(id = R.string.common_ui_onboarding_first_button_alt)
        else
          stringResource(id = R.string.common_ui_onboarding_first_button),
        style = ButtonLargeStyle.PRIMARY
      ) {
        if (pagerState.currentPage == (pages.size - 1))
          onResult.invoke()
        else {
          coroutineScope.launch {
            pagerState.animateScrollToPage(page = (pagerState.currentPage + 1))
          }
        }
      }

      if (pagerState.currentPage != (pages.size - 1)) {

        Spacer(modifier = Modifier.height(AppTheme.dimensions.mediumPadding))
        ButtonLarge(
          text = stringResource(id = R.string.common_ui_onboarding_second_button),
          style = ButtonLargeStyle.SECONDARY,
          onClick = {
            coroutineScope.launch {
              pagerState.animateScrollToPage(page = (pages.size - 1))
            }
          }
        )

      }
      Spacer(modifier = Modifier.height(AppTheme.dimensions.xxLargePadding))

    }
  }
}


@Composable
fun OnboardingPage(
  page: OnboardingPageData,
) {

  Column(
    modifier = Modifier
      .padding(horizontal = AppTheme.dimensions.xxxLargePadding),
    horizontalAlignment = Alignment.CenterHorizontally,
    verticalArrangement = Arrangement.Center
  ) {

    Image(
      modifier = Modifier
        .size(
          width = AppTheme.dimensions.onboardingImageWidth,
          height = AppTheme.dimensions.onboardingImageHeight
        ),
      painter = painterResource(id = page.imageResId),
      contentDescription = null
    )

    Spacer(modifier = Modifier.height(AppTheme.dimensions.xLargePadding))

    Text(
      text = page.title,
      style = AppTheme.typography.headline,
      color = AppTheme.colors.black,
      textAlign = TextAlign.Center
    )

    Spacer(modifier = Modifier.height(AppTheme.dimensions.regularPadding))

    Text(
      text = page.message,
      style = AppTheme.typography.body1Regular,
      color = AppTheme.colors.grey,
      textAlign = TextAlign.Center
    )

  }

}