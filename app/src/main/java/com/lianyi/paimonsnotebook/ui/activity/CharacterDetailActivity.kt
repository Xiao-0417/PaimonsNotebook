package com.lianyi.paimonsnotebook.ui.activity

import android.os.Bundle
import com.lianyi.paimonsnotebook.R
import com.lianyi.paimonsnotebook.lib.base.BaseActivity
import com.lianyi.paimonsnotebook.bean.CharacterBean
import com.lianyi.paimonsnotebook.databinding.ActivityCharacterDetailBinding
import com.lianyi.paimonsnotebook.databinding.PopInformationBinding
import com.lianyi.paimonsnotebook.lib.information.Area
import com.lianyi.paimonsnotebook.lib.information.Element
import com.lianyi.paimonsnotebook.lib.information.Star
import com.lianyi.paimonsnotebook.lib.information.WeaponType
import com.lianyi.paimonsnotebook.util.loadImage
import com.lianyi.paimonsnotebook.util.showAlertDialog

class CharacterDetailActivity : BaseActivity() {

    companion object{
        lateinit var character:CharacterBean
    }

    lateinit var bind: ActivityCharacterDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        bind = ActivityCharacterDetailBinding.inflate(layoutInflater)
        setContentView(bind.root)

        with(bind){
            area.text = Area.getNameByArea(character.area)
            characterProperty.text = Element.getNameByType(character.element)
            equipType.text = WeaponType.getNameByType(character.weaponType)
            name.text = character.name
            loadImage(icon, character.icon)
            starBackground.setImageResource(Star.getStarResourcesByStarNum(character.star,false))

            baseAtk.text = character.baseATK
            baseDef.text = character.baseDEF
            baseHp.text = character.baseHP

            upAttribute.text = character.upAttribute
            upAttributeValue.text = character.upAttributeValue

            Star.setStarBackgroundAndIcon(localMaterial, character.localMaterials.icon, character.localMaterials.star)
            Star.setStarBackgroundAndIcon(monsterMaterial, character.monsterMaterials.icon, character.monsterMaterials.star)
            Star.setStarBackgroundAndIcon(bossMaterial, character.bossMaterial.icon, character.bossMaterial.star)
            Star.setStarBackgroundAndIcon(dailyMaterial, character.dailyMaterials.icon, character.dailyMaterials.star)
            Star.setStarBackgroundAndIcon(weeklyMaterial, character.weeklyMaterials.icon, character.weeklyMaterials.star)

        }

        bind.information.setOnClickListener {
            val layout = PopInformationBinding.bind(layoutInflater.inflate(R.layout.pop_information,null))
            val win = showAlertDialog(bind.root.context,layout.root)
            layout.content.text = getString(R.string.information_entity_materials)
            layout.close.setOnClickListener {
                win.dismiss()
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.activity_fade_in,R.anim.activity_fade_out)
    }

}