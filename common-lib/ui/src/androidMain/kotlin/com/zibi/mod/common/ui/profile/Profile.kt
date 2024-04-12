package com.zibi.mod.common.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.zibi.mod.common.ui.theme.AppTheme
import com.zibi.mod.common.ui.R

@Composable
fun Profile(
  modifier: Modifier = Modifier,
  title: String,
  listOfProfiles: List<ProfileItem>? = null,
  onProfileClick: ((index: Int) -> Unit)? = null,
  iconButtonClose: @Composable () -> Unit,
  buttonPrimary: @Composable () -> Unit,
  buttonSecondary: @Composable () -> Unit,
  backgroundColor: Color = Color.White,
  elevation: Dp = AppTheme.dimensions.mediumPadding,
  maxHeight: Float = 1f,
  roundedCornerShape: Dp = AppTheme.dimensions.zero,
) {
  val smallPadding = AppTheme.dimensions.smallPadding
  val mediumPadding = AppTheme.dimensions.mediumPadding
  val regularPadding = AppTheme.dimensions.regularPadding
  val xlargePadding = AppTheme.dimensions.xLargePadding

  Box(
    modifier = modifier.fillMaxSize(),
    contentAlignment = Alignment.BottomCenter
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .fillMaxHeight(maxHeight),
      backgroundColor = backgroundColor,
      shape = RoundedCornerShape(
        roundedCornerShape,
        roundedCornerShape
      ),
      elevation = elevation
    ) {
      ConstraintLayout {
        val (iconCloseRef, titleRef, listContainerRef, buttonSettingsRef, buttonLogoutRef) = createRefs()
        Box(modifier = Modifier.constrainAs(iconCloseRef) {
          top.linkTo(titleRef.top)
          bottom.linkTo(titleRef.bottom)
          end.linkTo(
            parent.end,
            margin = smallPadding
          )
        }) {
          iconButtonClose()
        }
        Text(text = title,
          style = AppTheme.typography.title,
          modifier = Modifier.constrainAs(titleRef) {
            top.linkTo(
              parent.top,
              margin = xlargePadding
            )
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          })
        Column(modifier = Modifier
          .constrainAs(listContainerRef) {
            top.linkTo(
              iconCloseRef.bottom,
              margin = xlargePadding
            )
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            bottom.linkTo(
              buttonSettingsRef.top,
              margin = regularPadding
            )
            height = Dimension.fillToConstraints
          }
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.Top) {
          listOfProfiles?.forEachIndexed { index, profileItem ->
            val initials = when (profileItem.profileType) {
              ProfileType.ACTIVE,
              ProfileType.INACTIVE -> "${profileItem.name.take(1)}${profileItem.surname.take(1)}".uppercase()
              ProfileType.UNVERIFIED -> ""
            }

            val nameAndSurname = when (profileItem.profileType) {
              ProfileType.ACTIVE, ProfileType.INACTIVE -> stringResource(
                id = R.string.common_ui_profile_name_and_surname,
                profileItem.name,
                profileItem.surname
              )
              ProfileType.UNVERIFIED -> profileItem.name
            }

            val textColor = when (profileItem.profileType) {
              ProfileType.ACTIVE, ProfileType.UNVERIFIED -> AppTheme.colors.primary
              ProfileType.INACTIVE -> AppTheme.colors.black
            }
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .height(AppTheme.dimensions.profileItemHeight)
                .background(
                  when (profileItem.profileType) {
                    ProfileType.ACTIVE, ProfileType.UNVERIFIED -> AppTheme.colors.background
                    ProfileType.INACTIVE -> Color.White
                  },
                )
                .clickable { onProfileClick?.let { it(index) } },
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column(
                modifier = Modifier.padding(start = mediumPadding),
                horizontalAlignment = Alignment.End,
              ) {
                when (profileItem.profileType) {
                  ProfileType.ACTIVE, ProfileType.INACTIVE -> {
                    ProfileIcon(
                      text = initials,
                      showArrowDownIcon = profileItem.showArrowDownIcon,
                      onClick = {},
                    )
                  }
                  ProfileType.UNVERIFIED -> {
                    ProfileIcon(
                      showUserIcon = false,
                      showArrowDownIcon = profileItem.showArrowDownIcon,
                      onClick = {},
                    )
                  }
                }
              }
              Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(
                    start = regularPadding,
                    end = regularPadding
                  ),
                horizontalAlignment = Alignment.Start,
              ) {
                Text(
                  text = nameAndSurname,
                  style = AppTheme.typography.body1Regular,
                  color = textColor
                )
                if (profileItem.type.isNotEmpty()) {
                  Text(
                    text = profileItem.type,
                    style = AppTheme.typography.body2Regular,
                    color = textColor,
                  )
                }
              }
            }
          }
        }
        Box(modifier = Modifier
          .constrainAs(buttonSettingsRef) {
            bottom.linkTo(
              buttonLogoutRef.top,
              margin = regularPadding
            )
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          }
          .padding(
            start = AppTheme.dimensions.xRegularPadding,
            end = AppTheme.dimensions.xRegularPadding,
          )) {
          buttonPrimary()
        }
        Box(modifier = Modifier
          .constrainAs(buttonLogoutRef) {
            bottom.linkTo(
              parent.bottom,
              margin = xlargePadding,
            )
            start.linkTo(parent.start)
            end.linkTo(parent.end)
          }
          .padding(
            start = AppTheme.dimensions.xRegularPadding,
            end = AppTheme.dimensions.xRegularPadding,
          )) {
          buttonSecondary()
        }
      }
    }
  }
}