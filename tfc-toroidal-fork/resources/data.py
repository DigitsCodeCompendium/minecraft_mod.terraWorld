#  Work under Copyright. Licensed under the EUPL.
#  See the project README.md and LICENSE.txt for more information.

from mcresources import ResourceManager, utils, loot_tables
from constants import *



def generate(rm: ResourceManager):
    trim_material(rm, 'amethyst', '#9A5CC6', 'tfc:gem/amethyst', 0)
    trim_material(rm, 'diamond', '#6EECD2', 'tfc:gem/diamond', 0.1)
    trim_material(rm, 'emerald', '#11A036', 'tfc:gem/emerald', 0.2)
    trim_material(rm, 'lapis_lazuli', '#416E97', 'tfc:gem/lapis_lazuli', 0.3)
    trim_material(rm, 'opal', '#75e7eb', 'tfc:gem/opal', 0.4)
    trim_material(rm, 'pyrite', '#e6c44c', 'tfc:gem/pyrite', 0.4)
    trim_material(rm, 'ruby', '#971607', 'tfc:gem/ruby', 0.5)
    trim_material(rm, 'sapphire', '#183dde', 'tfc:gem/sapphire', 0.6)
    trim_material(rm, 'topaz', '#c27a0e', 'tfc:gem/topaz', 0.7)
    trim_material(rm, 'silver', '#edeadf', 'tfc:metal/ingot/silver', 0.8)
    trim_material(rm, 'sterling_silver', '#ccc7b6', 'tfc:metal/ingot/sterling_silver', 0.85)
    trim_material(rm, 'gold', '#DEB12D', 'tfc:metal/ingot/gold', 0.9)
    trim_material(rm, 'rose_gold', '#fcdd86', 'tfc:metal/ingot/rose_gold', 0.95)
    trim_material(rm, 'bismuth', '#8bbbc4', 'tfc:metal/ingot/bismuth', 1)

def trim_material(rm: ResourceManager, name: str, color: str, ingredient: str, item_model_index: float):
    rm.data(('trim_material', name), {
        'asset_name': name + '_' + rm.domain,  # this field is not properly namespaced, so we have to do that ourselves
        'description': {
            'color': color,
            'translate': 'trim_material.%s.%s' % (rm.domain, name)
        },
        'ingredient': ingredient,
        'item_model_index': item_model_index
    })
